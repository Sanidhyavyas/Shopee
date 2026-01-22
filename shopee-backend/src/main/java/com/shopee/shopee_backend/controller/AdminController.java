package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.dto.CreateFranchiseRequestDto;
import com.shopee.shopee_backend.dto.CreateFranchiseResponseDto;
import com.shopee.shopee_backend.dto.FranchiseDto;
import com.shopee.shopee_backend.dto.UserDto;
import com.shopee.shopee_backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Administrative management.
 * Provides endpoints for viewing users and managing franchise onboarding.
 * Base path: /admin
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor // Automatically injects AdminService via constructor
public class AdminController {

    private final AdminService adminService;

    /**
     * Endpoint: GET /admin/franchise
     * Fetches the complete list of franchises for the dashboard table.
     */

    @GetMapping("/franchise")
    public ResponseEntity<List<FranchiseDto>> getFranchiseList(){
        // ResponseEntity.ok() returns an HTTP 200 Status
        return ResponseEntity.ok(adminService.getAllFranchise());
    }

    /**
     * Endpoint: GET /admin/users
     * Fetches all registered users in the system.
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(){
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * Endpoint: POST /admin/franchise
     * Handles new franchise registration.
     * * @Valid: Triggers the Jakarta Validation constraints defined in CreateFranchiseRequestDto.
     * {@RequestBody} : Maps the incoming JSON data from the React frontend to the DTO.
     */
    @PostMapping("/franchise")
    public ResponseEntity<CreateFranchiseResponseDto> createFranchise(
            @RequestBody @Valid CreateFranchiseRequestDto createFranchiseRequestDto){
        // Returns 200 OK along with the newly created franchise credentials
        return ResponseEntity.ok(adminService.createFranchise(createFranchiseRequestDto));
    }
}