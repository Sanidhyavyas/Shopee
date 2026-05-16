package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LowStockAlertDto {

    private Long productId;
    private String productName;
    private String sku;
    private int stockQuantity;
    private int minStockAlert;
    private Long franchiseId;
    private String franchiseName;
    private String franchiseCity;
}
