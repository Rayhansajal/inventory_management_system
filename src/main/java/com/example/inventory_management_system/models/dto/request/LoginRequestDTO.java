package com.example.inventory_management_system.models.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank
    @Email
    private String email;
    @NotBlank @Size(min = 6)
    private String password;
}
