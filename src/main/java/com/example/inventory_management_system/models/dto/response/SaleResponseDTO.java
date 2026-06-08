package com.example.inventory_management_system.models.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class SaleResponseDTO {
    private Long id;
    private String invoiceNumber;
    private String customerName;
    private String customerEmail;
    private String createdByName;
    private BigDecimal subtotal;
    private BigDecimal taxPercent;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    private String notes;
    private List<SaleItemResponseDTO> items;
    private LocalDateTime createdAt;
}
