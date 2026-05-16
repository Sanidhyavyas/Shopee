package com.shopee.shopee_backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private String pincode;
    private String phone;

    // Franchise validity
    @Column(nullable = false)
    private LocalDate validFrom;

    @Column(nullable = false)
    private LocalDate validTo;

    // Status
    @Column(nullable = false)
    private boolean active = true;

    // Subscription plan: BASIC, STANDARD, PREMIUM
    @Column(nullable = false)
    private String subscriptionPlan = "BASIC";

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "franchise", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Product> products;

    @OneToMany(mappedBy = "franchise", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Order> orders;
}

