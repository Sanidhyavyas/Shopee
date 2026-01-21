package com.shopee.shopee_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Data Transfer Object for capturing new franchise registration details.
 * This class includes Jakarta Validation constraints to ensure data integrity
 * before the request reaches the Service layer.
 */
@Data
@NoArgsConstructor  // Required for deserializing JSON request bodies
@AllArgsConstructor // Useful for creating request objects in Integration Tests
public class CreateFranchiseRequestDto {

    @NotBlank(message = "Outlet name is required")
    private String outletName;

    @NotBlank(message = "Owner Name is required")
    private String ownerName;

    @NotBlank(message = "Owner email is required")
    private String email;

    // Ensures the string is exactly 10 characters, common for phone number validation
    @NotBlank(message = "Owner mobile is required")
    @Size(min = 10, max = 10, message = "Mobile must be 10 digits")
    private String mobile;

    @NotBlank(message = "Owner address is required")
    private String address;

    @NotBlank(message = "Owner city is required")
    private String city;

    @NotBlank(message = "Owner state is required")
    private String state;

    // Use @NotNull for objects like LocalDate (since @NotBlank only works for Strings)
    @NotNull(message = "Valid from date is required")
    private LocalDate validFrom;

    @NotNull(message = "Valid to date is required")
    private LocalDate validTo;
}