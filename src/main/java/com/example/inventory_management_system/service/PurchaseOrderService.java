package com.example.inventory_management_system.service;

import com.example.inventory_management_system.models.dto.request.PurchaseOrderRequestDTO;
import com.example.inventory_management_system.models.dto.response.PurchaseOrderResponseDTO;
import com.example.inventory_management_system.models.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseOrderService {
    PurchaseOrderResponseDTO create(PurchaseOrderRequestDTO request, String createdByEmail);
    PurchaseOrderResponseDTO findById(Long id);
    PurchaseOrderResponseDTO findByOrderNumber(String orderNumber);
    Page<PurchaseOrderResponseDTO> findAll(Pageable pageable);
    Page<PurchaseOrderResponseDTO> findByStatus(OrderStatus status, Pageable pageable);
    PurchaseOrderResponseDTO updateStatus(Long id, OrderStatus status);
    PurchaseOrderResponseDTO receiveOrder(Long id);
    void cancel(Long id);
}
