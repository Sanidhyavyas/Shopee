package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.AuditLogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface AuditLogService {

    /**
     * Persists an audit entry asynchronously. The current actor and IP are resolved
     * automatically from the security context and the active HTTP request.
     * Never throws — failures are swallowed and logged to SLF4J.
     */
    void log(String action, String entityType, String entityId,
             Object oldValue, Object newValue);

    Page<AuditLogDto> getLogsForEntity(String entityType, String entityId, Pageable pageable);

    Page<AuditLogDto> getLogsForFranchise(Long franchiseId, LocalDateTime from,
                                           LocalDateTime to, Pageable pageable);

    Page<AuditLogDto> getLogsByActor(Long actorId, Pageable pageable);
}
