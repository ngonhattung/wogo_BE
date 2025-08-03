package com.nhattung.wogo.security.jwt;

import com.nhattung.wogo.repository.RefreshTokenRepository;
import com.nhattung.wogo.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class TokenCleanupScheduler {

    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(TokenCleanupScheduler.class);

    @Scheduled(cron = "0 0 2 * * ?") // 2h AM every day
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            Date now = Date.from(LocalDateTime.now().atZone(java.time.ZoneId.systemDefault()).toInstant());

            // delete expired blacklisted tokens
            tokenBlacklistRepository.deleteByExpiryDateBefore(now);

            // delete expired refresh tokens
            refreshTokenRepository.deleteByExpiryDateBefore(now);

            logger.info("Token cleanup completed successfully at {}", LocalDateTime.now());

        } catch (Exception e) {
            logger.error("Error during token cleanup", e);
        }
    }
}
