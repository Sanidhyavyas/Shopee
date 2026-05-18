package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.CreateProductRequestDto;
import com.shopee.shopee_backend.dto.ProductDto;
import com.shopee.shopee_backend.dto.UpdateProductRequestDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> getProductsByFranchise(Long franchiseId);

    ProductDto getProduct(Long franchiseId, Long productId);

    ProductDto createProduct(Long franchiseId, CreateProductRequestDto request);

    ProductDto updateProduct(Long franchiseId, Long productId, UpdateProductRequestDto request);

    void deleteProduct(Long franchiseId, Long productId);

    List<ProductDto> getLowStockProducts(Long franchiseId);

    List<ProductDto> searchProducts(Long franchiseId, String keyword);
}
