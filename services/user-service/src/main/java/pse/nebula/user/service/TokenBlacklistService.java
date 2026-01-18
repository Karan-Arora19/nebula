package pse.nebula.user.service;

import io.jsonwebtoken.Claims;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pse.nebula.user.model.BlacklistedToken;
import pse.nebula.user.repository.BlacklistedTokenRepository;

@Service
@Slf4j
public class TokenBlacklistService {

    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtUtil jwtUtil;

    public TokenBlacklistService(BlacklistedTokenRepository blacklistedTokenRepository, JwtUtil jwtUtil) {
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public void blacklistToken(String token) {
        try {
            // Parse the token to get expiration time using JwtUtil
            Claims claims = jwtUtil.parseToken(token);
            Date expiration = claims.getExpiration();

            LocalDateTime expiresAt = expiration.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            // Check if token is already expired
            if (expiresAt.isAfter(LocalDateTime.now())) {
                // Save to database
                BlacklistedToken blacklistedToken = new BlacklistedToken(token, expiresAt);
                blacklistedTokenRepository.save(blacklistedToken);
                log.info("Token blacklisted successfully for user: {}, expires at: {}", 
                         claims.getSubject(), expiresAt);
            } else {
                log.warn("Token already expired, not adding to blacklist");
            }
        } catch (Exception e) {
            log.error("Error blacklisting token", e);
            throw new IllegalStateException("Failed to blacklist token", e);
        }
    }

    // Currently unused
    public boolean isBlacklisted(String token) {
        try {
            return blacklistedTokenRepository.existsByToken(token);
        } catch (Exception e) {
            log.error("Error checking blacklist", e);
            throw new IllegalStateException("Failed to check token blacklist", e);
        }
    }

    // Cleanup expired tokens every hour
    @Scheduled(fixedRate = 3600000) // 1 hour in milliseconds
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            blacklistedTokenRepository.deleteExpiredTokens(LocalDateTime.now());
            log.info("Cleaned up expired blacklisted tokens");
        } catch (Exception e) {
            log.error("Error cleaning up expired tokens", e);
        }
    }
}
