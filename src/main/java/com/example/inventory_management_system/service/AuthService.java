package com.example.inventory_management_system.service;

import com.example.inventory_management_system.models.dto.request.LoginRequestDTO;
import com.example.inventory_management_system.models.dto.request.RegisterRequestDTO;
import com.example.inventory_management_system.models.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterRequestDTO request);
    AuthResponseDTO login(LoginRequestDTO request);
    AuthResponseDTO refreshToken(String refreshToken);
}
