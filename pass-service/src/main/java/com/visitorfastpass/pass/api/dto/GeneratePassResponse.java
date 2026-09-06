package com.visitorfastpass.pass.api.dto;

import com.visitorfastpass.pass.domain.PassStatus;
import java.time.Instant;

public record GeneratePassResponse(
        Long passId,
        String passNumber,
        PassStatus status,
        String digitalPassUrl,
        Instant validFrom,
        Instant validUntil
) {
}
