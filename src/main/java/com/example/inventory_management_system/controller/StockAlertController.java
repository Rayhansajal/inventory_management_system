package com.example.inventory_management_system.controller;

import com.example.inventory_management_system.models.dto.response.ApiResponseDTO;
import com.example.inventory_management_system.models.dto.response.StockAlertResponseDTO;
import com.example.inventory_management_system.service.StockAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stock-alerts")
@RequiredArgsConstructor
@Tag(name = "Stock Alerts", description = "Stock alert management")
@SecurityRequirement(name = "bearerAuth")
public class StockAlertController {
    private final StockAlertService stockAlertService;

    @GetMapping
    @Operation(summary = "Get all active (unresolved) stock alerts")
    public ResponseEntity<ApiResponseDTO<Page<StockAlertResponseDTO>>> getActiveAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                stockAlertService.getActiveAlerts(
                        PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Manually resolve a stock alert")
    public ResponseEntity<ApiResponseDTO<Void>> resolve(@PathVariable Long id) {
        stockAlertService.resolveAlert(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Alert resolved"));
    }
}
