package com.shopee.shopee_backend.service;

import com.shopee.shopee_backend.dto.CreateProductRequestDto;
import com.shopee.shopee_backend.dto.ProductDto;
import com.shopee.shopee_backend.dto.UpdateProductRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    Page<ProductDto> getProductsByFranchise(Long franchiseId, Pageable pageable);

    ProductDto getProduct(Long franchiseId, Long productId);

    ProductDto createProduct(Long franchiseId, CreateProductRequestDto request);

    ProductDto updateProduct(Long franchiseId, Long productId, UpdateProductRequestDto request);

    void deleteProduct(Long franchiseId, Long productId);

    List<ProductDto> getLowStockProducts(Long franchiseId);

    List<ProductDto> searchProducts(Long franchiseId, String keyword);
}
