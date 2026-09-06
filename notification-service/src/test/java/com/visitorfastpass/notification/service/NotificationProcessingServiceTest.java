package com.visitorfastpass.notification.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.visitorfastpass.notification.domain.Notification;
import com.visitorfastpass.notification.event.PassGeneratedEvent;
import java.time.Instant;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class NotificationProcessingServiceTest {
    @Mock NotificationPersistenceService persistence;
    @Mock EmailDeliveryService emailDelivery;

    @Test
    void duplicateEventDoesNotSendAnotherEmail() {
        Notification existing = notification();
        PassGeneratedEvent event = event();
        when(persistence.createIfAbsent(event))
                .thenReturn(new NotificationPersistenceService.CreationResult(existing, false));

        new NotificationProcessingService(persistence, emailDelivery).process(event);

        verify(emailDelivery, never()).sendPassEmail(existing);
    }

    @Test
    void simulationModePersistsNotificationWithoutSmtp() {
        Notification notification = notification();
        PassGeneratedEvent event = event();
        when(persistence.createIfAbsent(event))
                .thenReturn(new NotificationPersistenceService.CreationResult(notification, true));
        when(emailDelivery.enabled()).thenReturn(false);

        new NotificationProcessingService(persistence, emailDelivery).process(event);

        verify(persistence).markSimulated(7L);
        verify(emailDelivery, never()).sendPassEmail(notification);
    }

    private Notification notification() {
        Notification notification = new Notification("event-1", 41L, "FP-1", "Riya", "riya@example.com",
                "Asha", "Meeting", OffsetDateTime.now().plusHours(1), Instant.now().plusSeconds(3600),
                "https://example.test/pass", "Approved");
        ReflectionTestUtils.setField(notification, "id", 7L);
        return notification;
    }

    private PassGeneratedEvent event() {
        return new PassGeneratedEvent("event-1", "PASS_GENERATED", Instant.now(), 41L, "VF-1", "FP-1",
                "https://example.test/pass", "Riya", "riya@example.com", "Asha", "Meeting",
                OffsetDateTime.now().plusHours(1), Instant.now().plusSeconds(3600));
    }
}
