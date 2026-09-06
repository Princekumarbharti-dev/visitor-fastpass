package com.visitorfastpass.visitor.api.dto;

import com.visitorfastpass.visitor.domain.VisitEventType;
import java.time.Instant;

public record VisitEventResponse(
        VisitEventType eventType,
        Long performedByUserId,
        String performedByName,
        String remarks,
        Instant eventTime
) {
}
