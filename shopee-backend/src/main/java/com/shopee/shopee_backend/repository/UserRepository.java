package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
     * * @param email The email to search for.
     * @return An Optional containing the User if found, or empty if not.
     */
    Optional<User> findByEmail(String email);
}