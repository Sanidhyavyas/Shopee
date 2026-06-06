package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    /** All non-revoked tokens for a user (used on logout to revoke active sessions). */
    List<RefreshToken> findAllByUserIdAndRevokedFalse(Long userId);

    /** All tokens for a user regardless of state (used on theft detection). */
    List<RefreshToken> findAllByUserId(Long userId);

    /**
     * Bulk-delete tokens that are both expired/revoked AND older than the retention
     * cutoff. Run by the cleanup job.
     */
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.createdAt < :cutoff " +
           "AND (rt.revoked = true OR rt.expiresAt < :now)")
    int deleteExpiredAndRevoked(LocalDateTime cutoff, LocalDateTime now);
}
