package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.config.SecurityUtils;
import com.shopee.shopee_backend.dto.CreateStaffRequestDto;
import com.shopee.shopee_backend.dto.StaffDto;
import com.shopee.shopee_backend.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/franchise/{franchiseId}/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<List<StaffDto>> getStaff(@PathVariable Long franchiseId) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        return ResponseEntity.ok(staffService.getStaffByFranchise(franchiseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<StaffDto> createStaff(
            @PathVariable Long franchiseId,
            @RequestBody @Valid CreateStaffRequestDto request) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(staffService.createStaff(franchiseId, request));
    }

    @DeleteMapping("/{staffId}")
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> removeStaff(
            @PathVariable Long franchiseId,
            @PathVariable Long staffId) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        staffService.removeStaff(franchiseId, staffId);
        return ResponseEntity.noContent().build();
    }
}
