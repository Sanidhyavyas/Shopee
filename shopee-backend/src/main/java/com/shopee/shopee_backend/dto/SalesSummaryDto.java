package com.shopee.shopee_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalesSummaryDto {

    private Long franchiseId;
    private String franchiseName;
    private LocalDate from;
    private LocalDate to;
    private long totalOrders;
    private long completedOrders;
    private long cancelledOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private List<TopProductDto> topProducts;
    private List<DailyRevenueDto> dailyRevenue;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProductDto {
        private String productName;
        private String sku;
        private long quantitySold;
        private BigDecimal revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyRevenueDto {
        private LocalDate date;
        private BigDecimal revenue;
        private long orderCount;
    }
}
