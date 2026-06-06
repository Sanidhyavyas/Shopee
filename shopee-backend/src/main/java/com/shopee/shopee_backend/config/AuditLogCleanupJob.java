package com.shopee.shopee_backend.config;

import com.shopee.shopee_backend.repository.AuditLogRepository;
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
public class AuditLogCleanupJob {

    private final AuditLogRepository auditLogRepository;

    @Value("${audit.log.retention-days:90}")
    private int retentionDays;

    @Scheduled(cron = "${audit.log.cleanup-cron:0 0 2 * * *}")
    @Transactional
    public void deleteExpiredAuditLogs() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(retentionDays);
        try {
            int deleted = auditLogRepository.deleteByTimestampBefore(cutoff);
            log.info("Audit log cleanup: deleted {} record(s) older than {} days (cutoff: {})",
                    deleted, retentionDays, cutoff);
        } catch (Exception ex) {
            log.error("Audit log cleanup failed: {}", ex.getMessage(), ex);
        }
    }
}
