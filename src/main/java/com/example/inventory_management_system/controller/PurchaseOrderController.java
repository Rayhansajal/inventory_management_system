package com.example.inventory_management_system.controller;

import com.example.inventory_management_system.models.dto.request.PurchaseOrderRequestDTO;
import com.example.inventory_management_system.models.dto.response.ApiResponseDTO;
import com.example.inventory_management_system.models.dto.response.PurchaseOrderResponseDTO;
import com.example.inventory_management_system.models.enums.OrderStatus;
import com.example.inventory_management_system.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Purchase Orders", description = "Purchase order lifecycle management")
@SecurityRequirement(name = "bearerAuth")
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new purchase order")
    public ResponseEntity<ApiResponseDTO<PurchaseOrderResponseDTO>> create(
            @Valid @RequestBody PurchaseOrderRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(
                        purchaseOrderService.create(request, userDetails.getUsername()),
                        "Purchase order created"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order by ID")
    public ResponseEntity<ApiResponseDTO<PurchaseOrderResponseDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(purchaseOrderService.findById(id)));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get purchase order by order number")
    public ResponseEntity<ApiResponseDTO<PurchaseOrderResponseDTO>> findByOrderNumber(
            @PathVariable String orderNumber) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                purchaseOrderService.findByOrderNumber(orderNumber)));
    }

    @GetMapping
    @Operation(summary = "Get all purchase orders (paginated)")
    public ResponseEntity<ApiResponseDTO<Page<PurchaseOrderResponseDTO>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                purchaseOrderService.findAll(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get purchase orders by status")
    public ResponseEntity<ApiResponseDTO<Page<PurchaseOrderResponseDTO>>> findByStatus(
            @PathVariable OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                purchaseOrderService.findByStatus(status, PageRequest.of(page, size))));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update purchase order status")
    public ResponseEntity<ApiResponseDTO<PurchaseOrderResponseDTO>> updateStatus(
            @PathVariable Long id, @RequestParam OrderStatus status) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                purchaseOrderService.updateStatus(id, status), "Status updated to " + status));
    }

    @PatchMapping("/{id}/receive")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Mark order as received and update stock")
    public ResponseEntity<ApiResponseDTO<PurchaseOrderResponseDTO>> receive(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                purchaseOrderService.receiveOrder(id), "Order received and stock updated"));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Cancel a purchase order")
    public ResponseEntity<ApiResponseDTO<Void>> cancel(@PathVariable Long id) {
        purchaseOrderService.cancel(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Order cancelled"));
    }
}
