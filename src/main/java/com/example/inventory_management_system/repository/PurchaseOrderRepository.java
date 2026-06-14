package com.example.inventory_management_system.repository;

import com.example.inventory_management_system.models.entity.PurchaseOrder;
import com.example.inventory_management_system.models.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);
    Page<PurchaseOrder> findByStatus(OrderStatus status, Pageable pageable);
    Page<PurchaseOrder> findBySupplierId(Long supplierId, Pageable pageable);
    boolean existsByOrderNumber(String orderNumber);
}
