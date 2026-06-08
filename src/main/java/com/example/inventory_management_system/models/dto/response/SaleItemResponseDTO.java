package com.example.inventory_management_system.models.dto.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class SaleItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
