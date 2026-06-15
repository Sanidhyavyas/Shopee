package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByActorId(Long actorId, Pageable pageable);

    Page<AuditLog> findByEntityTypeAndEntityId(String entityType, String entityId, Pageable pageable);

    Page<AuditLog> findByFranchiseIdAndTimestampBetween(
            Long franchiseId, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<AuditLog> findByAction(String action, Pageable pageable);

    @Modifying
    @Query("DELETE FROM AuditLog a WHERE a.timestamp < :cutoff")
    int deleteByTimestampBefore(LocalDateTime cutoff);
}
