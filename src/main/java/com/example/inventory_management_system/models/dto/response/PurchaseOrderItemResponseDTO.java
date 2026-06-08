package com.example.inventory_management_system.models.dto.response;

import com.example.inventory_management_system.models.enums.OrderStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class PurchaseOrderItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer orderedQuantity;
    private Integer receivedQuantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
