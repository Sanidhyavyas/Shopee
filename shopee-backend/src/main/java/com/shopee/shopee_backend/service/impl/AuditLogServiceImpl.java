package com.shopee.shopee_backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopee.shopee_backend.dto.AuditLogDto;
import com.shopee.shopee_backend.entity.AuditLog;
import com.shopee.shopee_backend.entity.User;
import com.shopee.shopee_backend.repository.AuditLogRepository;
import com.shopee.shopee_backend.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final com.shopee.shopee_backend.repository.UserRepository userRepository;

    @Async
    @Override
    public void log(String action, String entityType, String entityId,
                    Object oldValue, Object newValue) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()
                    || "anonymousUser".equals(authentication.getPrincipal())) {
                return;
            }

            String email = authentication.getName();
            User actor = userRepository.findByEmail(email).orElse(null);
            if (actor == null) {
                return;
            }

            String oldJson = toJson(oldValue);
            String newJson = toJson(newValue);
            String ip = resolveIp();

            Long franchiseId = resolveDefaultFranchiseId(actor);

            AuditLog entry = new AuditLog();
            entry.setActorId(actor.getUserId());
            entry.setActorEmail(actor.getEmail());
            entry.setActorRole(actor.getRole());
            entry.setAction(action);
            entry.setEntityType(entityType);
            entry.setEntityId(entityId);
            entry.setOldValue(oldJson);
            entry.setNewValue(newJson);
            entry.setIpAddress(ip);
            entry.setTimestamp(LocalDateTime.now());
            entry.setFranchiseId(franchiseId);

            auditLogRepository.save(entry);
        } catch (Exception ex) {
            log.error("Audit logging failed for action={} entityType={} entityId={}: {}",
                    action, entityType, entityId, ex.getMessage(), ex);
        }
    }

    @Override
    public Page<AuditLogDto> getLogsForEntity(String entityType, String entityId, Pageable pageable) {
        return auditLogRepository
                .findByEntityTypeAndEntityId(entityType, entityId, pageable)
                .map(this::toDto);
    }

    @Override
    public Page<AuditLogDto> getLogsForFranchise(Long franchiseId, LocalDateTime from,
                                                   LocalDateTime to, Pageable pageable) {
        return auditLogRepository
                .findByFranchiseIdAndTimestampBetween(franchiseId, from, to, pageable)
                .map(this::toDto);
    }

    @Override
    public Page<AuditLogDto> getLogsByActor(Long actorId, Pageable pageable) {
        return auditLogRepository.findByActorId(actorId, pageable).map(this::toDto);
    }

    // ---- helpers ----

    private String toJson(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            log.warn("Could not serialize audit value to JSON: {}", ex.getMessage());
            return value.toString();
        }
    }

    private String resolveIp() {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest request = attrs.getRequest();
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
            return request.getRemoteAddr();
        } catch (Exception ex) {
            log.warn("Could not resolve request IP: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * For STAFF, returns the assigned franchise. For FRANCHISE_ADMIN the first owned franchise
     * is used as context. SUPER_ADMIN has no single franchise context; returns null.
     */
    private Long resolveDefaultFranchiseId(User actor) {
        if ("STAFF".equals(actor.getRole()) && actor.getAssignedFranchise() != null) {
            return actor.getAssignedFranchise().getFranchiseId();
        }
        if ("FRANCHISE_ADMIN".equals(actor.getRole())
                && actor.getFranchises() != null
                && !actor.getFranchises().isEmpty()) {
            return actor.getFranchises().get(0).getFranchiseId();
        }
        return null;
    }

    private AuditLogDto toDto(AuditLog log) {
        return modelMapper.map(log, AuditLogDto.class);
    }
}
