package com.shopee.shopee_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for User information.
 * Used to send user details to the frontend without exposing sensitive entity data.
 */
@AllArgsConstructor // Enables quick object instantiation for testing and mapping
@NoArgsConstructor  // Required by frameworks like Jackson for JSON deserialization
@Data               // Bundles @Getter, @Setter, @ToString, @EqualsAndHashCode, and @RequiredArgsConstructor
public class UserDto {

    private Long userId;

    private String email;

    private String role;

    private boolean active; // Primitive boolean (defaults to false)
}