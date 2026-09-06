package com.visitorfastpass.visitor.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectionRequest(
        @NotBlank(message = "Rejection reason is required")
        @Size(min = 3, max = 300, message = "Rejection reason must contain 3 to 300 characters")
        String reason
) {
}
