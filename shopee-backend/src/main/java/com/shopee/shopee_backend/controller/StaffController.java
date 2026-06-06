package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.config.SecurityUtils;
import com.shopee.shopee_backend.dto.CreateStaffRequestDto;
import com.shopee.shopee_backend.dto.PagedResponseDto;
import com.shopee.shopee_backend.dto.StaffDto;
import com.shopee.shopee_backend.exception.ApiException;
import com.shopee.shopee_backend.service.StaffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/franchise/{franchiseId}/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<PagedResponseDto<StaffDto>> getStaff(
            @PathVariable Long franchiseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        if (size > 100) {
            throw new ApiException("Page size must not exceed 100", HttpStatus.BAD_REQUEST);
        }
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PagedResponseDto.of(staffService.getStaffByFranchise(franchiseId, pageable)));
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
