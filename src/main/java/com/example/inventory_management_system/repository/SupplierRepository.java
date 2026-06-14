package com.example.inventory_management_system.repository;

import com.example.inventory_management_system.models.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SupplierRepository extends JpaRepository<Supplier,Long> {
    boolean existsByEmail(String email);

    @Query("SELECT s FROM Supplier s WHERE s.active = true AND " +
            "(LOWER(s.name) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
            "LOWER(s.email) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Supplier> searchSuppliers(@Param("q") String query, Pageable pageable);

    Page<Supplier> findByActiveTrue(Pageable pageable);
}
