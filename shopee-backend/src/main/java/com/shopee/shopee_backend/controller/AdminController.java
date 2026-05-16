package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.dto.*;
import com.shopee.shopee_backend.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/franchise")
    public ResponseEntity<List<FranchiseDto>> getFranchiseList() {
        return ResponseEntity.ok(adminService.getAllFranchise());
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PostMapping("/franchise")
    public ResponseEntity<CreateFranchiseResponseDto> createFranchise(
            @RequestBody @Valid CreateFranchiseRequestDto createFranchiseRequestDto) {
        return ResponseEntity.ok(adminService.createFranchise(createFranchiseRequestDto));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/inventory/low-stock")
    public ResponseEntity<List<LowStockAlertDto>> getLowStockAlerts() {
        return ResponseEntity.ok(adminService.getLowStockAlerts());
    }

    @PutMapping("/franchise/{id}")
    public ResponseEntity<FranchiseDto> updateFranchise(
            @PathVariable Long id,
            @RequestBody @Valid UpdateFranchiseRequestDto request) {
        return ResponseEntity.ok(adminService.updateFranchise(id, request));
    }

    @PatchMapping("/franchise/{id}/status")
    public ResponseEntity<FranchiseDto> toggleFranchiseStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        return ResponseEntity.ok(adminService.toggleFranchiseStatus(id, active));
    }

    @PatchMapping("/users/{id}/status")
    public ResponseEntity<UserDto> updateUserStatus(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserStatusRequestDto request) {
        return ResponseEntity.ok(adminService.updateUserStatus(id, request));
    }
}
