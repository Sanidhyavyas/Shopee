package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long orderId;
    private Long franchiseId;
    private String franchiseName;
    private String customerName;
    private String customerMobile;
    private String status;
    private String notes;
    private BigDecimal totalAmount;
    private BigDecimal discountAmount;
    private String paymentMethod;
    private boolean paid;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
