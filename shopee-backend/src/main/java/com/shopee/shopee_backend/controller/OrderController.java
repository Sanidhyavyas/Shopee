package com.shopee.shopee_backend.controller;

import com.shopee.shopee_backend.config.SecurityUtils;
import com.shopee.shopee_backend.dto.CreateOrderRequestDto;
import com.shopee.shopee_backend.dto.OrderDto;
import com.shopee.shopee_backend.dto.UpdateOrderStatusRequestDto;
import com.shopee.shopee_backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/franchise/{franchiseId}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(@PathVariable Long franchiseId) {
        securityUtils.requireFranchiseAccess(franchiseId);
        return ResponseEntity.ok(orderService.getOrdersByFranchise(franchiseId));
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
}
