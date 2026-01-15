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
    private Long id;

    // Used as login (email)
    @Column(nullable = false, unique = true)
    private String email;

    // BCrypt hashed password
    @Column(nullable = false)
    private String password;

    // SUPER_ADMIN or FRANCHISE_ADMIN
    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // One user (franchise admin) can own multiple franchises
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Franchise> franchises;
}
