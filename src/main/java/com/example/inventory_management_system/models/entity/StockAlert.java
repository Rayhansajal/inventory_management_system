package com.example.inventory_management_system.models.entity;

import com.example.inventory_management_system.models.enums.AlertType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_alerts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;

    @Column(nullable = false)
    private Integer currentStock;

    @Column(nullable = false)
    private Integer thresholdLevel;

    @Column(nullable = false)
    private boolean resolved = false;

    private boolean emailSent = false;

    @Column(length = 500)
    private String message;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }
}
