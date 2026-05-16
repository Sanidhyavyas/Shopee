package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.config.SecurityUtils;
import com.shopee.shopee_backend.dto.CategoryDto;
import com.shopee.shopee_backend.dto.CreateCategoryRequestDto;
import com.shopee.shopee_backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/franchise/{franchiseId}/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getCategories(@PathVariable Long franchiseId) {
        securityUtils.requireFranchiseAccess(franchiseId);
        return ResponseEntity.ok(categoryService.getCategoriesByFranchise(franchiseId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<CategoryDto> createCategory(
            @PathVariable Long franchiseId,
            @RequestBody @Valid CreateCategoryRequestDto request) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(franchiseId, request));
    }

    @DeleteMapping("/{categoryId}")
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long franchiseId,
            @PathVariable Long categoryId) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        categoryService.deleteCategory(franchiseId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
