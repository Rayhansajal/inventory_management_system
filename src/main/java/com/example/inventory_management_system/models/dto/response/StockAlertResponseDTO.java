package com.example.inventory_management_system.models.dto.response;

import com.example.inventory_management_system.models.enums.AlertType;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class StockAlertResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private AlertType alertType;
    private Integer currentStock;
    private Integer thresholdLevel;
    private boolean resolved;
    private boolean emailSent;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
