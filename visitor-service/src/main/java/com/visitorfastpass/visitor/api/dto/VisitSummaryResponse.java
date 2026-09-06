package com.visitorfastpass.visitor.api.dto;

import com.visitorfastpass.visitor.domain.PassProvisioningStatus;
import com.visitorfastpass.visitor.domain.VisitStatus;
import java.time.Instant;
import java.time.OffsetDateTime;

public record VisitSummaryResponse(
        Long id,
        String publicReference,
        String visitorName,
        String visitorEmail,
        String visitorMobile,
        String organizationName,
        Long hostId,
        String hostName,
        String purpose,
        OffsetDateTime scheduledAt,
        VisitStatus status,
        PassProvisioningStatus passStatus,
        String passNumber,
        Instant checkInTime,
        Instant checkOutTime,
        Instant createdAt
) {
}
