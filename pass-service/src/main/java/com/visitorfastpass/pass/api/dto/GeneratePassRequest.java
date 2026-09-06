package com.visitorfastpass.pass.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record GeneratePassRequest(
        @NotNull @Positive Long visitId,
        @NotBlank @Size(max = 24) String publicReference,
        @NotBlank @Size(max = 100) String visitorName,
        @NotBlank @Email @Size(max = 120) String visitorEmail,
        @NotBlank @Size(max = 100) String hostName,
        @NotBlank @Size(max = 300) String purpose,
        @NotNull OffsetDateTime scheduledAt
) {
}
