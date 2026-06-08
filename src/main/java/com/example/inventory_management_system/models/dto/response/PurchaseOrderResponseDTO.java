package com.example.inventory_management_system.models.dto.response;

import com.example.inventory_management_system.models.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class PurchaseOrderResponseDTO {
    private Long id;
    private String orderNumber;
    private OrderStatus status;
    private Long supplierId;
    private String supplierName;
    private String createdByName;
    private BigDecimal totalAmount;
    private String notes;
    private LocalDate expectedDeliveryDate;
    private LocalDate actualDeliveryDate;
    private List<PurchaseOrderItemResponseDTO> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
