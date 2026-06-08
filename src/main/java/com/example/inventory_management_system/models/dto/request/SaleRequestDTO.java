package com.example.inventory_management_system.models.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class SaleRequestDTO {
    @Size(max = 150)
    private String customerName;

    @Email
    @Size(max = 100)
    private String customerEmail;

    @DecimalMin("0.00") @DecimalMax("100.00")
    private BigDecimal taxPercent = BigDecimal.ZERO;

    @Size(max = 500)
    private String notes;

    @NotEmpty
    @Valid
    private List<SaleItemRequest> items;

    @Data
    public static class SaleItemRequest {
        @NotNull
        private Long productId;
        @NotNull @Min(1)
        private Integer quantity;
        @NotNull @DecimalMin("0.00")
        private BigDecimal unitPrice;
    }
}
