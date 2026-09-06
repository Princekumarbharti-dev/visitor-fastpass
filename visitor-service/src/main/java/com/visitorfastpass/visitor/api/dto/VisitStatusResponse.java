package com.visitorfastpass.visitor.api.dto;

import com.visitorfastpass.visitor.domain.PassProvisioningStatus;
import com.visitorfastpass.visitor.domain.VisitStatus;
import java.time.Instant;
import java.time.OffsetDateTime;

public record VisitStatusResponse(
        String publicReference,
        String visitorName,
        String hostName,
        OffsetDateTime scheduledAt,
        VisitStatus status,
        PassProvisioningStatus passStatus,
        String passNumber,
        String decisionComment,
        Instant checkInTime,
        Instant checkOutTime
) {
}
