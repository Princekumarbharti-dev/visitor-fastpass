package com.visitorfastpass.visitor.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record VisitorRegistrationRequest(
        @NotBlank(message = "Full name is required")
        @Size(min = 2, max = 100, message = "Full name must contain 2 to 100 characters")
        @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Full name contains unsupported characters")
        String fullName,

        @NotBlank(message = "Mobile number is required")
        @Pattern(regexp = "^[0-9+() -]{7,20}$", message = "Mobile number format is invalid")
        String mobileNumber,

        @NotBlank(message = "Email address is required")
        @Email(message = "Email address is invalid")
        @Size(max = 120)
        String email,

        @NotBlank(message = "Organization name is required")
        @Size(max = 120)
        String organizationName,

        @NotBlank(message = "Purpose of visit is required")
        @Size(min = 3, max = 300)
        String purpose,

        @NotNull(message = "Host is required")
        @Positive(message = "Host ID must be positive")
        Long hostId,

        @NotNull(message = "Visit date and time are required")
        @Future(message = "Visit date and time must be in the future")
        OffsetDateTime scheduledAt
) {
}
