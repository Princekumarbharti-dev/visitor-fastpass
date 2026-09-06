package com.visitorfastpass.visitor.api.dto;

import jakarta.validation.constraints.Size;

public record ApprovalRequest(
        @Size(max = 300, message = "Approval comment cannot exceed 300 characters")
        String comment
) {
}
