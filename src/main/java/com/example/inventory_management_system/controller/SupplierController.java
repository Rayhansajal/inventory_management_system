package com.example.inventory_management_system.controller;

import com.example.inventory_management_system.models.dto.request.SupplierRequestDTO;
import com.example.inventory_management_system.models.dto.response.ApiResponseDTO;
import com.example.inventory_management_system.models.dto.response.SupplierResponseDTO;
import com.example.inventory_management_system.service.SupplierService;
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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Supplier management")
@SecurityRequirement(name = "bearerAuth")
public class SupplierController {
    private final SupplierService supplierService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new supplier")
    public ResponseEntity<ApiResponseDTO<SupplierResponseDTO>> create(
            @Valid @RequestBody SupplierRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(supplierService.create(request), "Supplier created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update supplier by ID")
    public ResponseEntity<ApiResponseDTO<SupplierResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody SupplierRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                supplierService.update(id, request), "Supplier updated"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    public ResponseEntity<ApiResponseDTO<SupplierResponseDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(supplierService.findById(id)));
    }

    @GetMapping
    @Operation(summary = "Get all active suppliers (paginated)")
    public ResponseEntity<ApiResponseDTO<Page<SupplierResponseDTO>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                supplierService.findAll(PageRequest.of(page, size, Sort.by(sortBy)))));
    }

    @GetMapping("/search")
    @Operation(summary = "Search suppliers by name or email")
    public ResponseEntity<ApiResponseDTO<Page<SupplierResponseDTO>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                supplierService.search(q, PageRequest.of(page, size))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate a supplier")
    public ResponseEntity<ApiResponseDTO<Void>> deactivate(@PathVariable Long id) {
        supplierService.deactivate(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Supplier deactivated"));
    }
}
