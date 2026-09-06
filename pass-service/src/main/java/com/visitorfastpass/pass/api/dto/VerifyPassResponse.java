package com.visitorfastpass.pass.api.dto;

import com.visitorfastpass.pass.domain.PassStatus;
import com.visitorfastpass.pass.domain.ScanResult;
import java.time.Instant;
import java.time.OffsetDateTime;

public record VerifyPassResponse(
        boolean valid,
        ScanResult result,
        Long visitId,
        String passNumber,
        String visitorName,
        String hostName,
        String purpose,
        OffsetDateTime scheduledAt,
        Instant validUntil,
        PassStatus status,
        String allowedAction
) {
}
