package com.shopee.shopee_backend.repository;

import com.shopee.shopee_backend.entity.Order;
import com.shopee.shopee_backend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByFranchiseFranchiseIdOrderByCreatedAtDesc(Long franchiseId);

    List<Order> findAllByFranchiseFranchiseIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long franchiseId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.franchise.franchiseId = :franchiseId AND o.status != 'CANCELLED'")
    BigDecimal sumRevenueByFranchiseId(Long franchiseId);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.status != 'CANCELLED'")
    BigDecimal sumTotalRevenue();

    long countByFranchiseFranchiseId(Long franchiseId);

    long countByStatus(OrderStatus status);

    long countByFranchiseFranchiseIdAndStatus(Long franchiseId, OrderStatus status);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.franchise.franchiseId = :franchiseId AND o.createdAt BETWEEN :from AND :to AND o.status != 'CANCELLED'")
    BigDecimal sumRevenueByFranchiseAndDateRange(Long franchiseId, LocalDateTime from, LocalDateTime to);
}
