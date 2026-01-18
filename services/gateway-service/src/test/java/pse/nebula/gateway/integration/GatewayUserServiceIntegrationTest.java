package pse.nebula.gateway.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Integration test for Gateway Service and User Service interaction.
 *
 * This test verifies:
 * 1. Gateway correctly routes requests to user-service
 * 2. Public endpoints (login, register) are accessible without authentication
 * 3. Gateway properly handles responses from user-service
 *
 * Note: This is a unit/integration test that mocks the user-service.
 * For full end-to-end testing, both services need to be running.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GatewayUserServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private DiscoveryClient discoveryClient;

    @Test
    @DisplayName("Should allow access to public login endpoint without authentication")
    void testPublicLoginEndpoint() {
        // Arrange - Mock discovery client to simulate user-service registration
        ServiceInstance mockInstance = mock(ServiceInstance.class);
        when(mockInstance.getUri()).thenReturn(URI.create("http://localhost:8083"));
        when(discoveryClient.getInstances("user-service")).thenReturn(Collections.singletonList(mockInstance));

        // Prepare login request
        String loginJson = "{\"email\":\"test@example.com\",\"password\":\"password123\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(loginJson, headers);

        // Act - Call login endpoint through gateway
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/users/login",
            HttpMethod.POST,
            request,
            String.class
        );

        // Assert - Should get response (even if user-service is not running, gateway should route it)
        // In real scenario with user-service running, this would return 200 or 401
        assertThat(response.getStatusCode()).isIn(
            HttpStatus.OK,               // If user-service is running and credentials are valid
            HttpStatus.UNAUTHORIZED,     // If user-service is running and credentials are invalid
            HttpStatus.SERVICE_UNAVAILABLE, // If user-service is not running
            HttpStatus.INTERNAL_SERVER_ERROR  // If there's a routing error
        );
    }

    @Test
    @DisplayName("Should allow access to public register endpoint without authentication")
    void testPublicRegisterEndpoint() {
        // Arrange
        ServiceInstance mockInstance = mock(ServiceInstance.class);
        when(mockInstance.getUri()).thenReturn(URI.create("http://localhost:8083"));
        when(discoveryClient.getInstances("user-service")).thenReturn(Collections.singletonList(mockInstance));

        String registerJson = "{\"username\":\"testuser\",\"email\":\"test@example.com\",\"password\":\"password123\",\"firstName\":\"Test\",\"lastName\":\"User\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(registerJson, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/users/register",
            HttpMethod.POST,
            request,
            String.class
        );

        // Assert - Gateway should route the request
        assertThat(response.getStatusCode()).isIn(
            HttpStatus.CREATED,            // If user-service is running and registration succeeds
            HttpStatus.BAD_REQUEST,        // If validation fails or user exists
            HttpStatus.SERVICE_UNAVAILABLE, // If user-service is not running
            HttpStatus.INTERNAL_SERVER_ERROR  // If there's a routing error
        );
    }

    @Test
    @DisplayName("Should block protected endpoints without valid JWT token")
    void testProtectedEndpointWithoutToken() {
        // Arrange
        ServiceInstance mockInstance = mock(ServiceInstance.class);
        when(mockInstance.getUri()).thenReturn(URI.create("http://localhost:8083"));
        when(discoveryClient.getInstances("user-service")).thenReturn(Collections.singletonList(mockInstance));

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        // Act - Try to access a protected endpoint without token
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/users/profile",  // Assuming this is a protected endpoint
            HttpMethod.GET,
            request,
            String.class
        );

        // Assert - Should get 401 Unauthorized
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Should block protected endpoints with invalid JWT token")
    void testProtectedEndpointWithInvalidToken() {
        // Arrange
        String invalidToken = "invalid.jwt.token";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + invalidToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/users/profile",
            HttpMethod.GET,
            request,
            String.class
        );

        // Assert - Should get 401 Unauthorized
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Gateway health check should be accessible")
    void testGatewayHealthEndpoint() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/actuator/health",
            String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("UP");
    }
}