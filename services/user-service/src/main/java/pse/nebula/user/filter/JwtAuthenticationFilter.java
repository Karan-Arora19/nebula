package pse.nebula.user.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // Define public endpoints that don't require authentication
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/users/register",
            "/api/users/login",
            "/api/users/.well-known/jwks.json",
            "/api/users/blacklist/check",
            "/actuator"
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.debug("Processing request for path: {}", path);

        // Skip authentication for public endpoints
        if (isPublicEndpoint(path)) {
            log.debug("Public endpoint detected, skipping JWT authentication: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // Extract headers from Gateway
        String userId = request.getHeader("X-User-Id");
        String email = request.getHeader("X-User-Email");
        String role = request.getHeader("X-User-Roles");

        log.debug("Received headers - UserId: {}, Email: {}, Role: {}", userId, email, role);

        // If user info is present (from Gateway), set authentication
        if (userId != null && !userId.isEmpty()) {
            // Create authority from role
            List<SimpleGrantedAuthority> authorities = Collections.emptyList();
            if (role != null && !role.isEmpty()) {
                authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
            }

            // Create authentication token
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            // Set in security context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("Authentication set for user: {} with role: {}", userId, role);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Check if the request path is a public endpoint
     * @param path The request URI path
     * @return true if the path is public, false otherwise
     */
    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }
}
