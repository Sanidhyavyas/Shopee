package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequestDto {

    private String name;
    private String description;
    private String barcode;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal costPrice;
    private Integer stockQuantity;
    private Integer minStockAlert;
    private Long categoryId;
    /** null means no change; false = soft-delete (deactivate). */
    private Boolean active;
}
