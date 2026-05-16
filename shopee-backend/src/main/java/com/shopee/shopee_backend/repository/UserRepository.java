package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import java.util.Optional;

/**
 * Repository interface for User entities.
 * Acts as the Persistence Layer, handling all database communication for user accounts.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Derived Query Method to find a user by their unique email address.
     * Used during authentication and to prevent duplicate registrations.
     */
    Optional<User> findByEmail(String email);

    /** Find all STAFF users assigned to a specific franchise. */
    List<User> findAllByRoleAndAssignedFranchiseFranchiseId(String role, Long franchiseId);

    /** Find all users by role (e.g. all FRANCHISE_ADMINs). */
    List<User> findAllByRole(String role);
}