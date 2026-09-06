package com.visitorfastpass.visitor.api.dto;

import com.visitorfastpass.visitor.domain.VisitStatus;
import java.time.OffsetDateTime;

public record VisitorRegistrationResponse(
        Long visitId,
        String publicReference,
        VisitStatus status,
        String hostName,
        OffsetDateTime scheduledAt,
        String message
) {
}
