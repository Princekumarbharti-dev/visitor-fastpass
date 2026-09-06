package com.visitorfastpass.visitor.api.dto;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class VisitorRegistrationRequestValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void acceptsCompleteValidRegistration() {
        var request = validRequest();
        assertThat(validator.validate(request)).isEmpty();
    }

    @Test
    void rejectsInvalidContactAndPastSchedule() {
        var request = new VisitorRegistrationRequest(
                "R", "12x", "invalid", "", "x", 0L, OffsetDateTime.now().minusMinutes(1));

        Set<String> fields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertThat(fields).contains("fullName", "mobileNumber", "email", "organizationName",
                "purpose", "hostId", "scheduledAt");
    }

    private VisitorRegistrationRequest validRequest() {
        return new VisitorRegistrationRequest(
                "Riya Mehta", "+91 9876543210", "riya@example.com", "Acme Pvt Ltd",
                "Product demonstration", 12L, OffsetDateTime.now().plusDays(1));
    }
}
