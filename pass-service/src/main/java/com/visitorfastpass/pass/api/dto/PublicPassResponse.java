package com.visitorfastpass.pass.api.dto;

import com.visitorfastpass.pass.domain.PassStatus;
import java.time.Instant;
import java.time.OffsetDateTime;

public record PublicPassResponse(
        String passNumber,
        String visitorName,
        String hostName,
        String purpose,
        OffsetDateTime scheduledAt,
        Instant validFrom,
        Instant validUntil,
        PassStatus status
) {
}
