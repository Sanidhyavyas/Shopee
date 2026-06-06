package com.shopee.shopee_backend.config;

import com.shopee.shopee_backend.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenCleanupJob {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${auth.refresh-token.cleanup-retain-days:7}")
    private int retainDays;

    @Scheduled(cron = "${auth.refresh-token.cleanup-cron:0 30 2 * * *}")
    @Transactional
    public void deleteExpiredAndRevokedTokens() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retainDays);
        LocalDateTime now = LocalDateTime.now();
        try {
            int deleted = refreshTokenRepository.deleteExpiredAndRevoked(cutoff, now);
            log.info("Refresh token cleanup: deleted {} record(s) older than {} days",
                    deleted, retainDays);
        } catch (Exception ex) {
            log.error("Refresh token cleanup failed: {}", ex.getMessage(), ex);
        }
    }
}
