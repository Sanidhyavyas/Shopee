package com.shopee.shopee_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Franchise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long franchiseId;

    // Outlet display name
    @Column(nullable = false)
    private String outletName;

    // Owner (FRANCHISE_ADMIN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Outlet location
    private String address;
    private String city;
    private String state;

    // Franchise validity
    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
