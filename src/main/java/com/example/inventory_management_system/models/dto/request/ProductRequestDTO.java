package com.example.inventory_management_system.models.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductRequestDTO {
    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String description;

    @NotBlank @Size(max = 50)
    private String sku;

    @Size(max = 50)
    private String barcode;

    @Size(max = 20)
    private String barcodeType;

    @Size(max = 100)
    private String category;

    @Size(max = 50)
    private String unit;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal costPrice;

    @NotNull @DecimalMin("0.00")
    private BigDecimal sellingPrice;

    @Min(0)
    private Integer quantityInStock = 0;

    @Min(0)
    private Integer minimumStockLevel = 10;

    @Min(0)
    private Integer maximumStockLevel = 1000;

    @Min(0)
    private Integer reorderPoint = 20;

    private Long supplierId;
}
