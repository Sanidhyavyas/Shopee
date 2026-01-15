package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {           //persistence layer

    Optional<User> findByEmail(String email);
}
