package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByFranchiseFranchiseIdAndActiveTrue(Long franchiseId);

    List<Product> findAllByFranchiseFranchiseId(Long franchiseId);

    /** Products whose stock is at or below their per-product minStockAlert threshold (franchise-scoped) */
    @Query("SELECT p FROM Product p WHERE p.franchise.franchiseId = :franchiseId AND p.stockQuantity <= p.minStockAlert AND p.active = true")
    List<Product> findLowStockByFranchise(Long franchiseId);

    /** System-wide low-stock products (used by admin dashboard) */
    @Query("SELECT p FROM Product p WHERE p.stockQuantity <= p.minStockAlert AND p.active = true")
    List<Product> findAllLowStock();

    Optional<Product> findBySkuAndFranchiseFranchiseId(String sku, Long franchiseId);

    boolean existsBySkuAndFranchiseFranchiseId(String sku, Long franchiseId);
}
