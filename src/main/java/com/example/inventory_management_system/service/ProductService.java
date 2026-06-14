package com.example.inventory_management_system.service;

import com.example.inventory_management_system.models.dto.request.ProductRequestDTO;
import com.example.inventory_management_system.models.dto.response.ProductResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {
    ProductResponseDTO create(ProductRequestDTO request);
    ProductResponseDTO update(Long id, ProductRequestDTO request);
    ProductResponseDTO findById(Long id);
    ProductResponseDTO findBySku(String sku);
    ProductResponseDTO findByBarcode(String barcode);
    Page<ProductResponseDTO> findAll(Pageable pageable);
    Page<ProductResponseDTO> search(String query, Pageable pageable);
    void delete(Long id);
    void adjustStock(Long id, int quantity, String reason);
    List<ProductResponseDTO> getLowStockProducts();
    List<ProductResponseDTO> getOutOfStockProducts();
}
