package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDto {

    private Long id;
    private Long actorId;
    private String actorEmail;
    private String actorRole;
    private String action;
    private String entityType;
    private String entityId;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private LocalDateTime timestamp;
    private Long franchiseId;
}
