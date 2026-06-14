package com.example.inventory_management_system.service.impl;

import com.example.inventory_management_system.exception.BadRequestException;
import com.example.inventory_management_system.exception.ResourceNotFoundException;
import com.example.inventory_management_system.models.dto.request.SupplierRequestDTO;
import com.example.inventory_management_system.models.dto.response.SupplierResponseDTO;
import com.example.inventory_management_system.models.entity.Supplier;
import com.example.inventory_management_system.repository.SupplierRepository;
import com.example.inventory_management_system.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public SupplierResponseDTO create(SupplierRequestDTO request) {
        if (request.getEmail() != null && supplierRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Supplier email already exists: " + request.getEmail());
        }
        Supplier supplier = modelMapper.map(request, Supplier.class);
        supplier.setId(null);
        return toResponseDTO(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public SupplierResponseDTO update(Long id, SupplierRequestDTO request) {
        Supplier supplier = findSupplierById(id);
        modelMapper.map(request, supplier);
        return toResponseDTO(supplierRepository.save(supplier));
    }

    @Override
    public SupplierResponseDTO findById(Long id) {
        return toResponseDTO(findSupplierById(id));
    }

    @Override
    public Page<SupplierResponseDTO> findAll(Pageable pageable) {
        return supplierRepository.findByActiveTrue(pageable).map(this::toResponseDTO);
    }

    @Override
    public Page<SupplierResponseDTO> search(String query, Pageable pageable) {
        return supplierRepository.searchSuppliers(query, pageable).map(this::toResponseDTO);
    }

    @Override
    @Transactional
    public void deactivate(Long id) {
        Supplier supplier = findSupplierById(id);
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }

    private Supplier findSupplierById(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", id));
    }

    private SupplierResponseDTO toResponseDTO(Supplier supplier) {
        SupplierResponseDTO dto = modelMapper.map(supplier, SupplierResponseDTO.class);
        dto.setTotalProducts(supplier.getProducts() != null ? supplier.getProducts().size() : 0);
        return dto;
    }
}
