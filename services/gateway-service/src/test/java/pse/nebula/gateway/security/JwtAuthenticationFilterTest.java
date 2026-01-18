package pse.nebula.gateway.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import pse.nebula.gateway.config.PublicRoutesConfig;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtAuthenticationFilter
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtValidator jwtValidator;

    @Mock
    private PublicRoutesConfig publicRoutesConfig;

    @Mock
    private GatewayFilterChain chain;

    // Added mock for TokenBlacklistClient to prevent NPEs
    @Mock
    private TokenBlacklistClient tokenBlacklistClient;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        // Default behavior: chain continues
        lenient().when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
        // Default blacklist check returns not blacklisted
        lenient().when(tokenBlacklistClient.isTokenBlacklisted(anyString())).thenReturn(Mono.just(false));
        // Ensure the filter has the mocked tokenBlacklistClient (explicit injection)
        filter.setTokenBlacklistClient(tokenBlacklistClient);
    }

    // Helper to ensure mock is injected for every test
    void ensureBlacklistClientInjected() {
        if (filter == null || tokenBlacklistClient == null) return;
        filter.setTokenBlacklistClient(tokenBlacklistClient);
    }

    @Test
    void testFilter_PublicRoute_BypassesAuthentication() {
        ensureBlacklistClientInjected();
        // Given: A public route
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/actuator/health")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(publicRoutesConfig.isPublicRoute("/actuator/health")).thenReturn(true);

        // When: Filtering the request
        Mono<Void> result = filter.filter(exchange, chain);

        // Then: Should bypass authentication and continue chain
        StepVerifier.create(result)
                .verifyComplete();

        verify(publicRoutesConfig).isPublicRoute("/actuator/health");
        verify(jwtValidator, never()).validateToken(anyString());
        verify(chain).filter(any(ServerWebExchange.class));
    }

    @Test
    void testFilter_MissingAuthorizationHeader_ReturnsUnauthorized() {
        ensureBlacklistClientInjected();
        // Given: A protected route without Authorization header
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/protected")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(publicRoutesConfig.isPublicRoute("/api/v1/protected")).thenReturn(false);

        // When: Filtering the request
        Mono<Void> result = filter.filter(exchange, chain);

        // Then: Should return 401 Unauthorized
        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(jwtValidator, never()).validateToken(anyString());
        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void testFilter_InvalidAuthorizationHeaderFormat_ReturnsUnauthorized() {
        ensureBlacklistClientInjected();
        // Given: A protected route with invalid Authorization header format
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/protected")
                .header(HttpHeaders.AUTHORIZATION, "InvalidFormat token123")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(publicRoutesConfig.isPublicRoute("/api/v1/protected")).thenReturn(false);

        // When: Filtering the request
        Mono<Void> result = filter.filter(exchange, chain);

        // Then: Should return 401 Unauthorized
        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(jwtValidator, never()).validateToken(anyString());
        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void testFilter_ValidToken_ContinuesWithUserHeaders() {
        ensureBlacklistClientInjected();
        // Given: A protected route with valid token
        String token = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims mockClaims = mock(Claims.class);
        lenient().when(mockClaims.getSubject()).thenReturn("user-123");
        lenient().when(mockClaims.get("email", String.class)).thenReturn("user@example.com");
        lenient().when(mockClaims.get("roles")).thenReturn("USER,ADMIN");

        when(publicRoutesConfig.isPublicRoute("/api/v1/protected")).thenReturn(false);
        when(jwtValidator.validateToken(token)).thenReturn(mockClaims);
        when(jwtValidator.getUserId(mockClaims)).thenReturn("user-123");
        when(jwtValidator.getEmail(mockClaims)).thenReturn("user@example.com");
        when(jwtValidator.getRoles(mockClaims)).thenReturn("USER,ADMIN");

        // When: Filtering the request
        Mono<Void> result = filter.filter(exchange, chain);

        // Then: Should validate token and add user headers
        StepVerifier.create(result)
                .verifyComplete();

        verify(jwtValidator).validateToken(token);
        verify(chain).filter(argThat(modifiedExchange -> {
            var headers = modifiedExchange.getRequest().getHeaders();
            return "user-123".equals(headers.getFirst("X-User-Id")) &&
                   "user@example.com".equals(headers.getFirst("X-User-Email")) &&
                   "USER,ADMIN".equals(headers.getFirst("X-User-Roles"));
        }));
    }

    @Test
    void testFilter_TokenValidationFails_ReturnsUnauthorized() {
        ensureBlacklistClientInjected();
        // Given: A protected route with invalid token
        String token = "invalid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(publicRoutesConfig.isPublicRoute("/api/v1/protected")).thenReturn(false);
        when(jwtValidator.validateToken(token)).thenThrow(new RuntimeException("Invalid token"));

        // When: Filtering the request
        Mono<Void> result = filter.filter(exchange, chain);

        // Then: Should return 401 Unauthorized
        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
        verify(jwtValidator).validateToken(token);
        verify(chain, never()).filter(any(ServerWebExchange.class));
    }

    @Test
    void testFilter_NullUserInfo_AddsEmptyHeaders() {
        ensureBlacklistClientInjected();
        // Given: Valid token but null user info
        String token = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims mockClaims = mock(Claims.class);

        when(publicRoutesConfig.isPublicRoute("/api/v1/protected")).thenReturn(false);
        when(jwtValidator.validateToken(token)).thenReturn(mockClaims);
        when(jwtValidator.getUserId(mockClaims)).thenReturn(null);
        when(jwtValidator.getEmail(mockClaims)).thenReturn(null);
        when(jwtValidator.getRoles(mockClaims)).thenReturn(null);

        // When: Filtering the request
        Mono<Void> result = filter.filter(exchange, chain);

        // Then: Should add empty string headers
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain).filter(argThat(modifiedExchange -> {
            var headers = modifiedExchange.getRequest().getHeaders();
            var userId = headers.getFirst("X-User-Id");
            var userEmail = headers.getFirst("X-User-Email");
            var userRoles = headers.getFirst("X-User-Roles");
            return userId != null && userId.isEmpty() &&
                   userEmail != null && userEmail.isEmpty() &&
                   userRoles != null && userRoles.isEmpty();
        }));
    }

    @Test
    void testFilter_DifferentHttpMethods_WorksForAll() {
        ensureBlacklistClientInjected();
        // Given: Setup for multiple HTTP methods
        String token = "valid.jwt.token";
        Claims mockClaims = mock(Claims.class);
        lenient().when(mockClaims.getSubject()).thenReturn("user-123");

        lenient().when(publicRoutesConfig.isPublicRoute("/api/v1/protected")).thenReturn(false);
        lenient().when(jwtValidator.validateToken(token)).thenReturn(mockClaims);
        lenient().when(jwtValidator.getUserId(mockClaims)).thenReturn("user-123");
        lenient().when(jwtValidator.getEmail(mockClaims)).thenReturn("test@example.com");
        lenient().when(jwtValidator.getRoles(mockClaims)).thenReturn("USER");

        // Test different HTTP methods
        testMethodWithValidToken("POST", token);
        testMethodWithValidToken("PUT", token);
        testMethodWithValidToken("DELETE", token);
        testMethodWithValidToken("PATCH", token);
    }

    @Test
    void testFilter_MultiplePublicRoutes_BypassesAll() {
        ensureBlacklistClientInjected();
        // Given: Multiple public routes
        var publicPaths = Arrays.asList(
                "/actuator/health",
                "/api/users/login",
                "/api/users/register"
        );

        for (String path : publicPaths) {
            MockServerHttpRequest request = MockServerHttpRequest.get(path).build();
            ServerWebExchange exchange = MockServerWebExchange.from(request);

            when(publicRoutesConfig.isPublicRoute(path)).thenReturn(true);

            // When: Filtering the request
            Mono<Void> result = filter.filter(exchange, chain);

            // Then: Should bypass authentication
            StepVerifier.create(result).verifyComplete();
        }

        verify(jwtValidator, never()).validateToken(anyString());
    }

    @Test
    void testGetOrder_ReturnsNegative100() {
        ensureBlacklistClientInjected();
        // When: Getting filter order
        int order = filter.getOrder();

        // Then: Should return -100 to run before other filters
        assertEquals(-100, order);
    }

    @Test
    void testFilter_CaseSensitivePath_WorksCorrectly() {
        ensureBlacklistClientInjected();
        // Given: Path with mixed case
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/Api/V1/Protected")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(publicRoutesConfig.isPublicRoute("/Api/V1/Protected")).thenReturn(false);

        // When: Filtering the request
        Mono<Void> result = filter.filter(exchange, chain);

        // Then: Should treat as protected route
        StepVerifier.create(result)
                .verifyComplete();

        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testFilter_PathWithQueryParams_WorksCorrectly() {
        ensureBlacklistClientInjected();
        // Given: Path with query parameters
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/routes/123?includeDetails=true")
                .header(HttpHeaders.AUTHORIZATION, "Bearer valid.token")
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims mockClaims = mock(Claims.class);
        lenient().when(mockClaims.getSubject()).thenReturn("user-123");

        when(publicRoutesConfig.isPublicRoute("/api/v1/routes/123")).thenReturn(false);
        when(jwtValidator.validateToken("valid.token")).thenReturn(mockClaims);
        when(jwtValidator.getUserId(mockClaims)).thenReturn("user-123");
        when(jwtValidator.getEmail(mockClaims)).thenReturn("test@example.com");
        when(jwtValidator.getRoles(mockClaims)).thenReturn("USER");

        // When: Filtering the request
        Mono<Void> result = filter.filter(exchange, chain);

        // Then: Should process path without query params
        StepVerifier.create(result)
                .verifyComplete();

        verify(jwtValidator).validateToken("valid.token");
    }

    @Test
    void testFilter_EmptyRoles_AddsEmptyRolesHeader() {
        ensureBlacklistClientInjected();
        // Given: Valid token with no roles
        String token = "valid.jwt.token";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Claims mockClaims = mock(Claims.class);
        lenient().when(mockClaims.getSubject()).thenReturn("user-123");

        when(publicRoutesConfig.isPublicRoute("/api/v1/protected")).thenReturn(false);
        when(jwtValidator.validateToken(token)).thenReturn(mockClaims);
        lenient().when(jwtValidator.getUserId(mockClaims)).thenReturn("user-123");
        lenient().when(jwtValidator.getEmail(mockClaims)).thenReturn("user@example.com");
        lenient().when(jwtValidator.getRoles(mockClaims)).thenReturn("");

        // When: Filtering the request
        Mono<Void> result = filter.filter(exchange, chain);

        // Then: Should add empty roles header
        StepVerifier.create(result)
                .verifyComplete();

        verify(chain).filter(argThat(modifiedExchange -> {
            var headers = modifiedExchange.getRequest().getHeaders();
            return "".equals(headers.getFirst("X-User-Roles"));
        }));
    }

    // Helper method
    private void testMethodWithValidToken(String method, String token) {
        MockServerHttpRequest request = MockServerHttpRequest
                .method(org.springframework.http.HttpMethod.valueOf(method), "/api/v1/protected")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Void> result = filter.filter(exchange, chain);

        StepVerifier.create(result).verifyComplete();
    }
}
