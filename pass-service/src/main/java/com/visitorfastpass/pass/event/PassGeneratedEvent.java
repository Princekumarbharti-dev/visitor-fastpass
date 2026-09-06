package com.visitorfastpass.pass.event;

import java.time.Instant;
import java.time.OffsetDateTime;

public record PassGeneratedEvent(
        String eventId,
        String eventType,
        Instant occurredAt,
        Long visitId,
        String publicReference,
        String passNumber,
        String digitalPassUrl,
        String visitorName,
        String visitorEmail,
        String hostName,
        String purpose,
        OffsetDateTime scheduledAt,
        Instant validUntil
) {
}
