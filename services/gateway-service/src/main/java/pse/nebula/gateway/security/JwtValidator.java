package pse.nebula.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT Validator for HMAC SHA256 signed tokens
 *
 * Uses the same secret key as the user-service for token validation.
 */
@Component
public class JwtValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtValidator.class);

    @Value("${jwt.secret:nebula-jwt-secret-key-change-in-production-minimum-32-characters-required}")
    private String jwtSecret;

    private JwtParser jwtParser;
    private WebClient webClient;

    public JwtValidator(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    public JwtValidator() {
    }

    /**
     * Initialize JWT parser with HMAC SHA256 signing key
     */
    @PostConstruct
    private void initialize() {
        try {
            SecretKey signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
            webClient = WebClient.builder().build(); // Initialize WebClient
            log.info("JWT validator initialized successfully with HMAC SHA256");
        } catch (Exception e) {
            log.error("Failed to initialize JWT validator", e);
            throw new IllegalStateException("JWT validator initialization failed", e);
        }
    }


    /**
     * Validate JWT token and extract claims
     */
    public Claims validateToken(String token) {
        try {
            Jws<Claims> jwsClaims = jwtParser.parseClaimsJws(token);
            Claims claims = jwsClaims.getBody();

            if (claims.getExpiration().before(new Date())) {
                log.warn("Token expired at: {}. Token details: {}", claims.getExpiration(), token);
                throw new IllegalArgumentException("Token expired");
            }

            log.debug("Token validated successfully for user: {}. Token details: {}", claims.getSubject(), token);
            return claims;
        } catch (IllegalArgumentException e) {
            log.error("Token validation failed: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during token validation", e);
            throw new IllegalStateException("Token validation failed", e);
        }
    }

    /**
     * Extract user ID from token
     */
    public String getUserId(Claims claims) {
        return claims.getSubject();
    }

    /**
     * Extract email from token
     */
    public String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }

    /**
     * Extract roles from token (optional)
     */
    public String getRoles(Claims claims) {
        Object roles = claims.get("roles");
        return roles != null ? roles.toString() : "";
    }

    public void setWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    void setJwtParser(JwtParser jwtParser) {
        this.jwtParser = jwtParser;
    }
}