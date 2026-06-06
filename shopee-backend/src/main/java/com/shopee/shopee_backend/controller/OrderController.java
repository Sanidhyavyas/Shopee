package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.config.SecurityUtils;
import com.shopee.shopee_backend.dto.CreateOrderRequestDto;
import com.shopee.shopee_backend.dto.OrderDto;
import com.shopee.shopee_backend.dto.PagedResponseDto;
import com.shopee.shopee_backend.dto.UpdateOrderStatusRequestDto;
import com.shopee.shopee_backend.entity.OrderStatus;
import com.shopee.shopee_backend.exception.ApiException;
import com.shopee.shopee_backend.service.OrderService;
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
@RequestMapping("/franchise/{franchiseId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<PagedResponseDto<OrderDto>> getOrders(
            @PathVariable Long franchiseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        securityUtils.requireFranchiseAccess(franchiseId);
        if (size > 100) {
            throw new ApiException("Page size must not exceed 100", HttpStatus.BAD_REQUEST);
        }
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PagedResponseDto.of(orderService.getOrdersByFranchise(franchiseId, pageable)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(
            @PathVariable Long franchiseId,
            @PathVariable Long orderId) {
        securityUtils.requireFranchiseAccess(franchiseId);
        return ResponseEntity.ok(orderService.getOrder(franchiseId, orderId));
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(
            @PathVariable Long franchiseId,
            @RequestBody @Valid CreateOrderRequestDto request) {
        securityUtils.requireFranchiseAccess(franchiseId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(franchiseId, request));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<OrderDto> updateStatus(
            @PathVariable Long franchiseId,
            @PathVariable Long orderId,
            @RequestBody @Valid UpdateOrderStatusRequestDto request) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        return ResponseEntity.ok(orderService.updateOrderStatus(franchiseId, orderId, request));
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasAnyRole('FRANCHISE_ADMIN','SUPER_ADMIN')")
    public ResponseEntity<OrderDto> cancelOrder(
            @PathVariable Long franchiseId,
            @PathVariable Long orderId) {
        securityUtils.requireFranchiseAdminAccess(franchiseId);
        return ResponseEntity.ok(orderService.cancelOrder(franchiseId, orderId));
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedResponseDto<OrderDto>> filterByStatus(
            @PathVariable Long franchiseId,
            @RequestParam OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        securityUtils.requireFranchiseAccess(franchiseId);
        if (size > 100) {
            throw new ApiException("Page size must not exceed 100", HttpStatus.BAD_REQUEST);
        }
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PagedResponseDto.of(orderService.getOrdersByStatus(franchiseId, status, pageable)));
    }
}
