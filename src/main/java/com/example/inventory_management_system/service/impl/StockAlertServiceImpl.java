
package com.example.inventory_management_system.service.impl;
import com.example.inventory_management_system.exception.ResourceNotFoundException;
import com.example.inventory_management_system.models.dto.response.StockAlertResponseDTO;
import com.example.inventory_management_system.models.entity.Product;
import com.example.inventory_management_system.models.entity.StockAlert;
import com.example.inventory_management_system.models.enums.AlertType;
import com.example.inventory_management_system.repository.StockAlertRepository;
import com.example.inventory_management_system.service.StockAlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class StockAlertServiceImpl implements StockAlertService {
    private final StockAlertRepository stockAlertRepository;
    private final JavaMailSender mailSender;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public void checkAndCreateAlerts(Product product) {
        if (product.getQuantityInStock() == 0) {
            createAlertIfAbsent(product, AlertType.OUT_OF_STOCK,
                    "Product '" + product.getName() + "' is OUT OF STOCK");
        } else if (product.getQuantityInStock() <= product.getMinimumStockLevel()) {
            createAlertIfAbsent(product, AlertType.LOW_STOCK,
                    "Product '" + product.getName() + "' is LOW on stock. Current: "
                            + product.getQuantityInStock() + ", Min: " + product.getMinimumStockLevel());
        } else {
            List<StockAlert> existing = stockAlertRepository.findByProductIdAndResolvedFalse(product.getId());
            existing.forEach(a -> {
                a.setResolved(true);
                a.setResolvedAt(LocalDateTime.now());
            });
            stockAlertRepository.saveAll(existing);
        }
    }

    private void createAlertIfAbsent(Product product, AlertType type, String message) {
        boolean exists = stockAlertRepository
                .existsByProductIdAndAlertTypeAndResolvedFalse(product.getId(), type);
        if (!exists) {
            StockAlert alert = StockAlert.builder()
                    .product(product)
                    .alertType(type)
                    .currentStock(product.getQuantityInStock())
                    .thresholdLevel(product.getMinimumStockLevel())
                    .message(message)
                    .resolved(false)
                    .emailSent(false)
                    .build();
            stockAlertRepository.save(alert);
            log.info("Stock alert created: {}", message);
        }
    }

    @Override
    public Page<StockAlertResponseDTO> getActiveAlerts(Pageable pageable) {
        return stockAlertRepository.findByResolvedFalse(pageable).map(this::toResponseDTO);
    }

    @Override
    @Transactional
    public void resolveAlert(Long alertId) {
        StockAlert alert = stockAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("StockAlert", alertId));
        alert.setResolved(true);
        alert.setResolvedAt(LocalDateTime.now());
        stockAlertRepository.save(alert);
    }

    @Override
    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void sendPendingEmailAlerts() {
        List<StockAlert> pending = stockAlertRepository.findByEmailSentFalseAndResolvedFalse();
        for (StockAlert alert : pending) {
            try {
                SimpleMailMessage mail = new SimpleMailMessage();
                mail.setTo("admin@iwms.com");
                mail.setSubject("[IWMS Alert] " + alert.getAlertType() + " - " + alert.getProduct().getName());
                mail.setText(alert.getMessage()
                        + "\n\nProduct SKU: " + alert.getProduct().getSku()
                        + "\nCurrent Stock: " + alert.getCurrentStock()
                        + "\nMinimum Level: " + alert.getThresholdLevel()
                        + "\nTime: " + alert.getCreatedAt());
                mailSender.send(mail);
                alert.setEmailSent(true);
                stockAlertRepository.save(alert);
                log.info("Alert email sent for product: {}", alert.getProduct().getName());
            } catch (Exception e) {
                log.error("Failed to send alert email: {}", e.getMessage());
            }
        }
    }

    private StockAlertResponseDTO toResponseDTO(StockAlert alert) {
        StockAlertResponseDTO dto = modelMapper.map(alert, StockAlertResponseDTO.class);
        dto.setProductId(alert.getProduct().getId());
        dto.setProductName(alert.getProduct().getName());
        dto.setProductSku(alert.getProduct().getSku());
        return dto;
    }
}
