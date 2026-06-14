package com.example.inventory_management_system.repository;

import com.example.inventory_management_system.models.entity.StockAlert;
import com.example.inventory_management_system.models.enums.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface StockAlertRepository extends JpaRepository<StockAlert, Long> {
    Page<StockAlert> findByResolvedFalse(Pageable pageable);
    List<StockAlert> findByProductIdAndResolvedFalse(Long productId);
    List<StockAlert> findByEmailSentFalseAndResolvedFalse();
    boolean existsByProductIdAndAlertTypeAndResolvedFalse(Long productId, AlertType alertType);
}
