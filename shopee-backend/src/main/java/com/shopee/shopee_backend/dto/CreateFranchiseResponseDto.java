package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for the API response after creating a franchise.
 * Carries the generated ID, credentials, and success/error feedback to the client.
 */
@Data
@AllArgsConstructor // Allows the Service layer to return a fully populated response in one line
@NoArgsConstructor  // Necessary for framework-level object creation (serialization)
public class CreateFranchiseResponseDto {

    // The primary key of the newly created owner/franchise for navigation in React
    private Long franchiseId;

    private String email;

    // Stores the raw UUID password only during creation; not persisted in this form in DB
    private String temporaryPassword;

    // A human-readable status message for UI alerts or notifications
    private String message;
}