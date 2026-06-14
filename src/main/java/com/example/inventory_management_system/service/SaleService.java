package com.example.inventory_management_system.service;

import com.example.inventory_management_system.models.dto.request.SaleRequestDTO;
import com.example.inventory_management_system.models.dto.response.SaleResponseDTO;
import com.example.inventory_management_system.models.dto.response.SalesReportResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface SaleService {
    SaleResponseDTO create(SaleRequestDTO request, String createdByEmail);
    SaleResponseDTO findById(Long id);
    SaleResponseDTO findByInvoiceNumber(String invoiceNumber);
    Page<SaleResponseDTO> findAll(Pageable pageable);
    Page<SaleResponseDTO> findByDateRange(LocalDate from, LocalDate to, Pageable pageable);
    SalesReportResponseDTO generateReport(LocalDate from, LocalDate to);
}
