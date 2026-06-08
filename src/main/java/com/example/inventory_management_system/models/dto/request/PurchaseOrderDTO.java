package com.example.inventory_management_system.models.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
public class PurchaseOrderDTO {

    @NotNull
    private Long supplierId;

    @Size(max = 500)
    private String notes;

    private LocalDate expectedDeliveryDate;

    @NotEmpty
    @Valid
    private List<PurchaseOrderItemRequest> items;

    @Data
    public static class PurchaseOrderItemRequest {
        @NotNull
        private Long productId;
        @NotNull @Min(1)
        private Integer orderedQuantity;
        @NotNull
        @DecimalMin("0.00")
        private BigDecimal unitPrice;
    }
}
