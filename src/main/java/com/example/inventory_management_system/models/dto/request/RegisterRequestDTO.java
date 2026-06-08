package com.example.inventory_management_system.models.dto.request;

import com.example.inventory_management_system.models.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank
    @Size(max = 100)
    private String fullName;
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 6, max = 50)
    private String password;
    @NotNull
    private Role role;
}
