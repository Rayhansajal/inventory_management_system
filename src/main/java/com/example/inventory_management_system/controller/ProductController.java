package com.example.inventory_management_system.controller;

import com.example.inventory_management_system.models.dto.request.ProductRequestDTO;
import com.example.inventory_management_system.models.dto.response.ApiResponseDTO;
import com.example.inventory_management_system.models.dto.response.ProductResponseDTO;
import com.example.inventory_management_system.service.ProductService;
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

import java.util.List;
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product inventory management")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new product")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> create(
            @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(productService.create(request), "Product created"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update product by ID")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> update(
            @PathVariable Long id, @Valid @RequestBody ProductRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success(productService.update(id, request), "Product updated"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDTO.success(productService.findById(id)));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(ApiResponseDTO.success(productService.findBySku(sku)));
    }

    @GetMapping
    @Operation(summary = "Get all products (paginated)")
    public ResponseEntity<ApiResponseDTO<Page<ProductResponseDTO>>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                productService.findAll(PageRequest.of(page, size, Sort.by(sortBy)))));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products by name, SKU or category")
    public ResponseEntity<ApiResponseDTO<Page<ProductResponseDTO>>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                productService.search(q, PageRequest.of(page, size))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete a product")
    public ResponseEntity<ApiResponseDTO<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Product deleted"));
    }

    @PatchMapping("/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'WAREHOUSE_STAFF')")
    @Operation(summary = "Adjust stock quantity (+/-)")
    public ResponseEntity<ApiResponseDTO<Void>> adjustStock(
            @PathVariable Long id,
            @RequestParam int quantity,
            @RequestParam(defaultValue = "Manual adjustment") String reason) {
        productService.adjustStock(id, quantity, reason);
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Stock adjusted by " + quantity));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get all low-stock products")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getLowStock() {
        return ResponseEntity.ok(ApiResponseDTO.success(productService.getLowStockProducts()));
    }

    @GetMapping("/out-of-stock")
    @Operation(summary = "Get all out-of-stock products")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getOutOfStock() {
        return ResponseEntity.ok(ApiResponseDTO.success(productService.getOutOfStockProducts()));
    }
}
