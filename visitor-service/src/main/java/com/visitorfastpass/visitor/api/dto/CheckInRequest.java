package com.visitorfastpass.visitor.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CheckInRequest(
        @NotBlank(message = "QR token is required") @Size(max = 512) String token,
        @Size(max = 100) String location
) {}
