package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Franchise entity.
 * Provides the abstraction layer for database operations, allowing
 * for CRUD actions without writing manual SQL queries.
 */
@Repository // Marks this as a Data Access Object (DAO) in the Spring Context
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    // JpaRepository provides built-in methods like:
    // .findAll(), .findById(), .save(), and .deleteById()

    /** Find all franchises owned by a specific user (FRANCHISE_ADMIN). */
    List<Franchise> findAllByOwnerUserId(Long userId);

    /** Check if a franchise belongs to a specific owner — used for access validation. */
    boolean existsByFranchiseIdAndOwnerUserId(Long franchiseId, Long userId);

    /** Active franchises only. */
    List<Franchise> findAllByActiveTrue();
}