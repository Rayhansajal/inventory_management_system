package com.example.inventory_management_system.models.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String sku;
    private String barcode;
    private String barcodeType;
    private String category;
    private String unit;
    private BigDecimal costPrice;
    private BigDecimal sellingPrice;
    private Integer quantityInStock;
    private Integer minimumStockLevel;
    private Integer maximumStockLevel;
    private Integer reorderPoint;
    private boolean active;
    private boolean lowStock;
    private Long supplierId;
    private String supplierName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
