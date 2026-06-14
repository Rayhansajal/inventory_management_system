package com.example.inventory_management_system.service.impl;

import com.example.inventory_management_system.exception.BadRequestException;
import com.example.inventory_management_system.models.dto.request.LoginRequestDTO;
import com.example.inventory_management_system.models.dto.request.RegisterRequestDTO;
import com.example.inventory_management_system.models.dto.response.AuthResponseDTO;
import com.example.inventory_management_system.models.entity.User;
import com.example.inventory_management_system.repository.UserRepository;
import com.example.inventory_management_system.security.JwtUtils;
import com.example.inventory_management_system.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();
        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponseDTO refreshToken(String refreshToken) {
        String email = jwtUtils.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
        if (!jwtUtils.isTokenValid(refreshToken, user)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        return buildAuthResponse(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        String accessToken  = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);
        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtils.getJwtExpiration())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .build();
    }
}
