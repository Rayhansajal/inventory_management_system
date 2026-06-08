package com.example.inventory_management_system.models.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(unique = true, nullable = false, length = 50)
    private String sku;

    @Column(unique = true, length = 50)
    private String barcode;

    @Column(length = 20)
    private String barcodeType; // EAN13, QR, CODE128, UPC

    @Column(length = 100)
    private String category;

    @Column(length = 50)
    private String unit; // pcs, kg, liter

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(nullable = false)
    private Integer quantityInStock = 0;

    @Column(nullable = false)
    private Integer minimumStockLevel = 10;

    @Column(nullable = false)
    private Integer maximumStockLevel = 1000;

    @Column(nullable = false)
    private Integer reorderPoint = 20;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<StockAlert> stockAlerts = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
