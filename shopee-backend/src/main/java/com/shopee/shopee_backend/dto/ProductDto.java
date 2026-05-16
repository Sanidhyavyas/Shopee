package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private Long productId;
    private String name;
    private String description;
    private String sku;
    private String barcode;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Integer stockQuantity;
    private Integer minStockAlert;
    private Long categoryId;
    private String categoryName;
    private Long franchiseId;
    private boolean active;
    private boolean lowStock;
    private LocalDateTime createdAt;
}
