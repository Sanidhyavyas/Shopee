package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.config.SecurityUtils;
import com.shopee.shopee_backend.dto.CreateProductRequestDto;
import com.shopee.shopee_backend.dto.ProductDto;
import com.shopee.shopee_backend.dto.UpdateProductRequestDto;
import com.shopee.shopee_backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/franchise/{franchiseId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<ProductDto>> getProducts(@PathVariable Long franchiseId) {
        securityUtils.requireFranchiseAccess(franchiseId);
        return ResponseEntity.ok(productService.getProductsByFranchise(franchiseId));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(
            @PathVariable Long franchiseId,
            @PathVariable Long productId) {
        securityUtils.requireFranchiseAccess(franchiseId);
        return ResponseEntity.ok(productService.getProduct(franchiseId, productId));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<ProductDto>> getLowStock(@PathVariable Long franchiseId) {
        securityUtils.requireFranchiseAccess(franchiseId);
        return ResponseEntity.ok(productService.getLowStockProducts(franchiseId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(
            @PathVariable Long franchiseId,
            @RequestParam String keyword) {
        securityUtils.requireFranchiseAccess(franchiseId);
        return ResponseEntity.ok(productService.searchProducts(franchiseId, keyword));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ProductDto> createProduct(
            @PathVariable Long franchiseId,
            @RequestBody @Valid CreateProductRequestDto request) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(franchiseId, request));
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long franchiseId,
            @PathVariable Long productId,
            @RequestBody UpdateProductRequestDto request) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        return ResponseEntity.ok(productService.updateProduct(franchiseId, productId, request));
    }

    @DeleteMapping("/{productId}")
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable Long franchiseId,
            @PathVariable Long productId) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        productService.deleteProduct(franchiseId, productId);
        return ResponseEntity.noContent().build();
    }
}
