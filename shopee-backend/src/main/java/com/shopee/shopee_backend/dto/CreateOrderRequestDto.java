package com.shopee.shopee_backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequestDto {

    private String customerName;
    private String customerMobile;
    private String notes;
    private String paymentMethod = "CASH";
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private boolean paid = false;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemRequestDto> items;
}
