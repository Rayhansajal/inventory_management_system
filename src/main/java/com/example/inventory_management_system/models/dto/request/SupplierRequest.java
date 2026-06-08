package com.example.inventory_management_system.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SupplierRequest {
    @NotBlank
    @Size(max = 150)
    private String name;

    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 300)
    private String address;

    @Size(max = 100)
    private String contactPerson;

    @Size(max = 50)
    private String taxNumber;
}
