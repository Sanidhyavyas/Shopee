package com.shopee.shopee_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column
    private String name;

    // Login + identity
    @Column(nullable = false, unique = true)
    private String email;

    // Contact for owner
    @Column(nullable = false)
    private String mobile;

    // BCrypt hashed password
    @Column(nullable = false)
    private String password;

    // SUPER_ADMIN, FRANCHISE_ADMIN, STAFF
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Account locking after failed login attempts
    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    @Column(nullable = false)
    private boolean accountLocked = false;

    private LocalDateTime lockExpiry;

    // Refresh token (stored hashed in production)
    private String refreshToken;

    // OTP for password reset
    private String resetOtp;
    private LocalDateTime resetOtpExpiry;

    // For STAFF: link to franchise they work at
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_franchise_id")
    private Franchise assignedFranchise;

    // One user (franchise admin) can own multiple franchises
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Franchise> franchises;
}

