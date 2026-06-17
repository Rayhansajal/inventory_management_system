package com.example.inventory_management_system.controller;

import com.example.inventory_management_system.models.dto.request.LoginRequestDTO;
import com.example.inventory_management_system.models.dto.request.RegisterRequestDTO;
import com.example.inventory_management_system.models.dto.response.ApiResponseDTO;
import com.example.inventory_management_system.models.dto.response.AuthResponseDTO;
import com.example.inventory_management_system.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, and token refresh")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> register(
            @Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.success(authService.register(request), "User registered successfully"));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate and receive JWT tokens")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(ApiResponseDTO.success(authService.login(request), "Login successful"));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<ApiResponseDTO<AuthResponseDTO>> refresh(
            @RequestParam String refreshToken) {
        return ResponseEntity.ok(ApiResponseDTO.success(
                authService.refreshToken(refreshToken), "Token refreshed"));
    }
}
