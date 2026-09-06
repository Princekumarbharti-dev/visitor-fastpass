package com.visitorfastpass.notification.api.dto;

import com.visitorfastpass.notification.domain.NotificationStatus;
import com.visitorfastpass.notification.domain.NotificationType;
import java.time.Instant;
import java.time.OffsetDateTime;

public record NotificationResponse(
        Long id,
        String eventId,
        Long visitId,
        String passNumber,
        String recipientName,
        String recipientEmail,
        String digitalPassUrl,
        NotificationType type,
        NotificationStatus status,
        OffsetDateTime scheduledAt,
        int retryCount,
        String failureReason,
        Instant createdAt,
        Instant sentAt
) {}
