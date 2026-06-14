package com.example.inventory_management_system.service;

import com.example.inventory_management_system.models.dto.response.ProductResponseDTO;

public interface BarcodeService {
    ProductResponseDTO lookupByBarcode(String barcode);
    String generateBarcode(String sku);
}
