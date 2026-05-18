package com.shopee.shopee_backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequestDto {

    @Size(min = 1, message = "Product name must not be blank if provided")
    private String name;

    private String description;
    private String barcode;
    private String imageUrl;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @DecimalMin(value = "0.0", message = "Cost price cannot be negative")
    private BigDecimal costPrice;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @Min(value = 0, message = "Min stock alert cannot be negative")
    private Integer minStockAlert;

    private Long categoryId;

    /** null means no change; false = soft-delete (deactivate). */
    private Boolean active;
}

