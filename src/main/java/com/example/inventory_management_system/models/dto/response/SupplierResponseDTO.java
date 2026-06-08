package com.example.inventory_management_system.models.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class SupplierResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String contactPerson;
    private String taxNumber;
    private boolean active;
    private int totalProducts;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

