package com.shopee.shopee_backend.service.impl;

import com.shopee.shopee_backend.dto.CreateProductRequestDto;
import com.shopee.shopee_backend.dto.ProductDto;
import com.shopee.shopee_backend.dto.UpdateProductRequestDto;
import com.shopee.shopee_backend.entity.Category;
import com.shopee.shopee_backend.entity.Franchise;
import com.shopee.shopee_backend.entity.Product;
import com.shopee.shopee_backend.exception.ApiException;
import com.shopee.shopee_backend.exception.ResourceNotFoundException;
import com.shopee.shopee_backend.repository.CategoryRepository;
import com.shopee.shopee_backend.repository.FranchiseRepository;
import com.shopee.shopee_backend.repository.ProductRepository;
import com.shopee.shopee_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final FranchiseRepository franchiseRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByFranchise(Long franchiseId) {
        return productRepository.findAllByFranchiseFranchiseIdAndActiveTrue(franchiseId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDto getProduct(Long franchiseId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        requireSameFranchise(product, franchiseId);
        return toDto(product);
    }

    @Override
    @Transactional
    public ProductDto createProduct(Long franchiseId, CreateProductRequestDto request) {
        Franchise franchise = franchiseRepository.findById(franchiseId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise not found: " + franchiseId));

        String sku = request.getSku();
        if (sku == null || sku.isBlank()) {
            sku = "SKU-" + franchiseId + "-" + (System.currentTimeMillis() % 1_000_000);
        } else if (productRepository.existsBySkuAndFranchiseFranchiseId(sku, franchiseId)) {
            throw new ApiException("SKU '" + sku + "' already exists in this franchise", HttpStatus.CONFLICT);
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(sku);
        product.setBarcode(request.getBarcode());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());
        product.setCostPrice(request.getCostPrice());
        product.setStockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0);
        product.setMinStockAlert(request.getMinStockAlert() != null ? request.getMinStockAlert() : 10);
        product.setFranchise(franchise);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));
            product.setCategory(category);
        }

        return toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long franchiseId, Long productId, UpdateProductRequestDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        requireSameFranchise(product, franchiseId);

        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getBarcode() != null) product.setBarcode(request.getBarcode());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getCostPrice() != null) product.setCostPrice(request.getCostPrice());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getMinStockAlert() != null) product.setMinStockAlert(request.getMinStockAlert());
        if (request.getActive() != null) product.setActive(request.getActive());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));
            product.setCategory(category);
        }

        return toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long franchiseId, Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        requireSameFranchise(product, franchiseId);
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getLowStockProducts(Long franchiseId) {
        return productRepository.findLowStockByFranchise(franchiseId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // ---- helpers ----

    private void requireSameFranchise(Product product, Long franchiseId) {
        if (!product.getFranchise().getFranchiseId().equals(franchiseId)) {
            throw new ApiException("Product does not belong to franchise " + franchiseId, HttpStatus.FORBIDDEN);
        }
    }

    private ProductDto toDto(Product p) {
        Long categoryId = p.getCategory() != null ? p.getCategory().getCategoryId() : null;
        String categoryName = p.getCategory() != null ? p.getCategory().getName() : null;
        boolean lowStock = p.getStockQuantity() <= p.getMinStockAlert();
        return new ProductDto(
                p.getProductId(), p.getName(), p.getDescription(),
                p.getSku(), p.getBarcode(), p.getImageUrl(),
                p.getPrice(), p.getCostPrice(),
                p.getStockQuantity(), p.getMinStockAlert(),
                categoryId, categoryName,
                p.getFranchise().getFranchiseId(),
                p.isActive(), lowStock, p.getCreatedAt());
    }
}
