package com.mockinterview.auth.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long userId,
        String email,
        String fullName,
        String role
) {}
