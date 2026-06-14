package com.example.inventory_management_system.service;

import com.example.inventory_management_system.models.dto.response.StockAlertResponseDTO;
import com.example.inventory_management_system.models.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockAlertService {
    void checkAndCreateAlerts(Product product);
    Page<StockAlertResponseDTO> getActiveAlerts(Pageable pageable);
    void resolveAlert(Long alertId);
    void sendPendingEmailAlerts();
}
