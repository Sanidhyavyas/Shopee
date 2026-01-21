package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Franchise entity.
 * Provides the abstraction layer for database operations, allowing
 * for CRUD actions without writing manual SQL queries.
 */
@Repository // Marks this as a Data Access Object (DAO) in the Spring Context
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    // JpaRepository provides built-in methods like:
    // .findAll(), .findById(), .save(), and .deleteById()
}