package com.example.inventory_management_system.repository;

import com.example.inventory_management_system.models.entity.SaleTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface SaleTransactionRepository extends JpaRepository<SaleTransaction, Long> {
    Optional<SaleTransaction> findByInvoiceNumber(String invoiceNumber);

    Page<SaleTransaction> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM SaleTransaction s WHERE s.createdAt BETWEEN :from AND :to")
    BigDecimal sumTotalRevenue(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT COUNT(s) FROM SaleTransaction s WHERE s.createdAt BETWEEN :from AND :to")
    Long countTransactions(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("SELECT si.product.id, si.product.name, si.product.sku, " +
            "SUM(si.quantity), SUM(si.totalPrice) " +
            "FROM SaleItem si WHERE si.saleTransaction.createdAt BETWEEN :from AND :to " +
            "GROUP BY si.product.id, si.product.name, si.product.sku " +
            "ORDER BY SUM(si.quantity) DESC")
    List<Object[]> findTopProducts(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);
}
