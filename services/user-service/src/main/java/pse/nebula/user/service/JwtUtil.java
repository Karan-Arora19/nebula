package pse.nebula.user.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class for JWT token generation and validation.
 * Uses HMAC SHA256 for signing tokens with a secret key.
 */
@Component
@Slf4j
public class JwtUtil {

    private static final int MIN_SECRET_LENGTH = 32;
    private static final long ACCESS_TOKEN_TTL_SECONDS = 3600; // 1 hour

    @Value("${jwt.secret:default-secret-key-change-in-production-must-be-at-least-32-chars}")
    private String jwtSecret;

    private volatile SecretKey signingKey;

    /**
     * Generates a JWT access token for the given user.
     *
     * @param userId User's unique identifier
     * @param email User's email address
     * @param role User's role
     * @return JWT access token
     */
    public String generateToken(String userId, String email, String role) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(ACCESS_TOKEN_TTL_SECONDS);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("role", role);

        String token = Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(userId)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .claim("email", email)
                .claim("role", role)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();

        log.debug("Generated JWT token for user: {}", email);
        return token;
    }

    /**
     * Parses and validates a JWT token, extracting its claims.
     *
     * @param token JWT token to parse
     * @return Claims from the token
     * @throws io.jsonwebtoken.JwtException if token is invalid
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Extracts the expiration time from a JWT token.
     *
     * @param token JWT token
     * @return Expiration instant
     * @throws io.jsonwebtoken.JwtException if token is invalid
     */
    public Instant getTokenExpiration(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().toInstant();
    }

    /**
     * Extracts the token ID (jti) from a JWT token.
     *
     * @param token JWT token
     * @return Token ID
     * @throws io.jsonwebtoken.JwtException if token is invalid
     */
    public String getTokenId(String token) {
        Claims claims = parseToken(token);
        return claims.getId();
    }

    /**
     * Gets or creates the signing key from the configured secret.
     * Uses double-checked locking for thread-safe lazy initialization.
     *
     * @return SecretKey for signing JWT tokens
     */
    private SecretKey getSigningKey() {
        if (signingKey != null) {
            return signingKey;
        }

        synchronized (this) {
            if (signingKey != null) {
                return signingKey;
            }

            validateSecret(jwtSecret);
            signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            log.info("JWT signing key initialized with {} bit strength", signingKey.getEncoded().length * 8);
            return signingKey;
        }
    }

    /**
     * Validates that the JWT secret meets minimum security requirements.
     *
     * @param secret JWT secret to validate
     * @throws IllegalArgumentException if secret is too short
     */
    private void validateSecret(String secret) {
        if (secret == null || secret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("JWT secret must be at least %d characters, got %d",
                            MIN_SECRET_LENGTH, secret != null ? secret.length() : 0)
            );
        }
    }
}