package com.visitorfastpass.pass.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RevokePassRequest(
        @NotBlank @Size(min = 3, max = 300) String reason
) {
}
