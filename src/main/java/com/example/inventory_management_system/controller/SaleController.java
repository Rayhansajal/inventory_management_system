package com.example.inventory_management_system.controller;

import com.example.inventory_management_system.models.dto.request.SaleRequestDTO;
import com.example.inventory_management_system.models.dto.response.ApiResponseDTO;
import com.example.inventory_management_system.models.dto.response.SaleResponseDTO;
import com.example.inventory_management_system.models.dto.response.SalesReportResponseDTO;
import com.example.inventory_management_system.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
@Tag(name = "Sales", description = "Sales transactions and reports")
@SecurityRequirement(name = "bearerAuth")
public class SaleController {
    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Record a new sale transaction")
    public ResponseEntity<ApiResponseDTO<SaleResponseDTO>> create(
            @Valid @RequestBody SaleRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(
                        saleService.create(request, userDetails.getUsername()),
                        "Sale recorded successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sale by ID")
    public ResponseEntity<ApiResponseDTO<SaleResponseDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(saleService.findById(id)));
    }

    @GetMapping("/invoice/{invoiceNumber}")
    @Operation(summary = "Get sale by invoice number")
    public ResponseEntity<ApiResponseDTO<SaleResponseDTO>> findByInvoice(
            @PathVariable String invoiceNumber) {
        return ResponseEntity.ok(ApiResponseDTO.success(saleService.findByInvoiceNumber(invoiceNumber)));
    }

    @GetMapping
    @Operation(summary = "Get all sales (paginated)")
    public ResponseEntity<ApiResponseDTO<Page<SaleResponseDTO>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                saleService.findAll(PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/range")
    @Operation(summary = "Get sales by date range")
    public ResponseEntity<ApiResponseDTO<Page<SaleResponseDTO>>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                saleService.findByDateRange(from, to, PageRequest.of(page, size))));
    }

    @GetMapping("/report")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate sales report for a date range")
    public ResponseEntity<ApiResponseDTO<SalesReportResponseDTO>> generateReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                saleService.generateReport(from, to), "Report generated"));
    }
}
