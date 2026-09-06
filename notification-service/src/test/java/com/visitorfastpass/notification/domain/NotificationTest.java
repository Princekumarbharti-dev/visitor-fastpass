package com.visitorfastpass.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class NotificationTest {
    @Test
    void tracksFailureRetryAndSuccess() {
        Notification notification = notification();
        notification.markFailed("SMTP unavailable");
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.FAILED);
        assertThat(notification.getRetryCount()).isEqualTo(1);

        notification.prepareRetry();
        notification.markSent();
        assertThat(notification.getStatus()).isEqualTo(NotificationStatus.SENT);
        assertThat(notification.getSentAt()).isNotNull();
    }

    private Notification notification() {
        return new Notification("event-1", 41L, "FP-1", "Riya", "riya@example.com", "Asha",
                "Meeting", OffsetDateTime.now().plusHours(1), Instant.now().plusSeconds(3600),
                "https://example.test/pass", "Approved");
    }
}
