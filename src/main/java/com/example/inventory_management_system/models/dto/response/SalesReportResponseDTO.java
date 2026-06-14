package com.example.inventory_management_system.models.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
@Builder
public class SalesReportResponseDTO {
    private String reportPeriod;
    private Long totalTransactions;
    private BigDecimal totalRevenue;
    private BigDecimal totalTax;
    private BigDecimal netRevenue;
    private Integer totalItemsSold;
    private List<TopProductResponseDTO> topProducts;
    private List<DailySalesSummaryDTO> dailySummary;

    @Data @Builder
    public static class TopProductResponseDTO {
        private Long productId;
        private String productName;
        private String sku;
        private Integer totalQuantitySold;
        private BigDecimal totalRevenue;
    }

    @Data @Builder
    public static class DailySalesSummaryDTO {
        private String date;
        private Long transactionCount;
        private BigDecimal revenue;
    }
}
