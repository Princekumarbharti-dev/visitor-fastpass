package com.visitorfastpass.pass.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record VerifyPassRequest(
        @NotBlank(message = "QR token is required") @Size(max = 512) String token,
        @Size(max = 100) String location
) {
}
