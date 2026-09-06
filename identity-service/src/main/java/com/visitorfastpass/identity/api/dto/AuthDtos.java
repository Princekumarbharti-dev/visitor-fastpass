package com.visitorfastpass.identity.api.dto;

import jakarta.validation.constraints.NotBlank;

public final class AuthDtos {
    private AuthDtos() {}

    public record LoginRequest(
            @NotBlank(message = "Username is required") String username,
            @NotBlank(message = "Password is required") String password
    ) {}

    public record LoginResponse(
            String accessToken,
            String tokenType,
            long expiresInSeconds,
            String role,
            Long employeeId,
            String displayName
    ) {}
}
