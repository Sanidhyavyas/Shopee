package com.shopee.shopee_backend.dto;

import com.shopee.shopee_backend.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderStatusRequestDto {

    @NotNull(message = "Status is required")
    private OrderStatus status;

    private Boolean paid;
}
