package com.example.inventory_management_system.service;

import com.example.inventory_management_system.models.dto.request.SupplierRequestDTO;
import com.example.inventory_management_system.models.dto.response.SupplierResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {
    SupplierResponseDTO create(SupplierRequestDTO request);
    SupplierResponseDTO update(Long id, SupplierRequestDTO request);
    SupplierResponseDTO findById(Long id);
    Page<SupplierResponseDTO> findAll(Pageable pageable);
    Page<SupplierResponseDTO> search(String query, Pageable pageable);
    void deactivate(Long id);
}
