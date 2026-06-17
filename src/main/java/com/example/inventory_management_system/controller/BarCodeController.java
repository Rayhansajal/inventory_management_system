package com.example.inventory_management_system.controller;

import com.example.inventory_management_system.models.dto.response.ApiResponseDTO;
import com.example.inventory_management_system.models.dto.response.ProductResponseDTO;
import com.example.inventory_management_system.service.BarcodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/barcodes")
@RequiredArgsConstructor
@Tag(name = "Barcodes", description = "Barcode lookup and generation")
@SecurityRequirement(name = "bearerAuth")
public class BarCodeController {

    private final BarcodeService barcodeService;

    @GetMapping("/lookup/{barcode}")
    @Operation(summary = "Look up a product by its barcode value")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> lookup(@PathVariable String barcode) {
        return ResponseEntity.ok(ApiResponseDTO.success(barcodeService.lookupByBarcode(barcode)));
    }

    @GetMapping("/generate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Generate a barcode number for a given SKU")
    public ResponseEntity<ApiResponseDTO<String>> generate(@RequestParam String sku) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                barcodeService.generateBarcode(sku), "Barcode generated for SKU: " + sku));
    }
}
