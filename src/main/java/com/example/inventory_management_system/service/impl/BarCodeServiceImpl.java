package com.example.inventory_management_system.service.impl;

import com.example.inventory_management_system.models.dto.response.ProductResponseDTO;
import com.example.inventory_management_system.service.BarcodeService;
import com.example.inventory_management_system.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BarCodeServiceImpl implements BarcodeService {
    private final ProductService productService;

    @Override
    public ProductResponseDTO lookupByBarcode(String barcode) {
        return productService.findByBarcode(barcode);
    }

    @Override
    public String generateBarcode(String sku) {
        long hash = Math.abs((long) sku.hashCode()) % 100_000_000_000L;
        String body = String.format("%011d", hash);
        int checksum = computeEan13Checksum(body);
        String barcode = body + checksum;
        log.info("Generated barcode {} for SKU {}", barcode, sku);
        return barcode;
    }

    private int computeEan13Checksum(String digits11) {
        int sum = 0;
        for (int i = 0; i < digits11.length(); i++) {
            int d = Character.getNumericValue(digits11.charAt(i));
            sum += (i % 2 == 0) ? d : d * 3;
        }
        return (10 - (sum % 10)) % 10;
    }
}
