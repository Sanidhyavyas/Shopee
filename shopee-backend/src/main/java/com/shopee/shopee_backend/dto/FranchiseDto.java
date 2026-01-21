package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Franchise details.
 * Primarily used to display franchise summary information on the Admin Dashboard.
 */
@AllArgsConstructor // Facilitates easy mapping from the Franchise entity in Service logic
@NoArgsConstructor  // Essential for Jackson to recreate this object from incoming JSON requests
@Data               // Generates boilerplate: getters, setters, toString, and value-based equality logic
public class FranchiseDto {

    private Long id;

    private String outletName;

    private String city;

    private String state;

    private String email;

    private String mobile;
}