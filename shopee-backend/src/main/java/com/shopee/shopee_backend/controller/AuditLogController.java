package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.dto.AuditLogDto;
import com.shopee.shopee_backend.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<Page<AuditLogDto>> getLogsForEntity(
            @PathVariable String entityType,
            @PathVariable String entityId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogsForEntity(entityType, entityId, pageable));
    }

    @GetMapping("/franchise/{franchiseId}")
    public ResponseEntity<Page<AuditLogDto>> getLogsForFranchise(
            @PathVariable Long franchiseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogsForFranchise(franchiseId, from, to, pageable));
    }

    @GetMapping("/actor/{actorId}")
    public ResponseEntity<Page<AuditLogDto>> getLogsByActor(
            @PathVariable Long actorId,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(auditLogService.getLogsByActor(actorId, pageable));
    }
}
