package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDto {

    private long totalFranchises;
    private long activeFranchises;
    private long totalUsers;
    private long totalOrders;
    private long pendingOrders;
    private BigDecimal totalRevenue;
    private long lowStockProducts;
}
