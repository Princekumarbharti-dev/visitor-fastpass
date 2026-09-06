package com.visitorfastpass.notification.service;

import com.visitorfastpass.notification.domain.Notification;
import com.visitorfastpass.notification.event.PassGeneratedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationProcessingService {
    private static final Logger log = LoggerFactory.getLogger(NotificationProcessingService.class);
    private final NotificationPersistenceService persistence;
    private final EmailDeliveryService emailDelivery;

    public NotificationProcessingService(NotificationPersistenceService persistence,
                                         EmailDeliveryService emailDelivery) {
        this.persistence = persistence;
        this.emailDelivery = emailDelivery;
    }

    public void process(PassGeneratedEvent event) {
        var result = persistence.createIfAbsent(event);
        if (!result.created()) {
            log.info("Ignoring duplicate notification event {}", event.eventId());
            return;
        }
        deliver(result.notification());
    }

    public Notification retry(long notificationId) {
        Notification notification = persistence.prepareRetry(notificationId);
        deliver(notification);
        return persistence.required(notificationId);
    }

    private void deliver(Notification notification) {
        if (!emailDelivery.enabled()) {
            persistence.markSimulated(notification.getId());
            log.info("Simulated pass email {} to {}", notification.getEventId(), notification.getRecipientEmail());
            return;
        }
        try {
            emailDelivery.sendPassEmail(notification);
            persistence.markSent(notification.getId());
        } catch (RuntimeException ex) {
            persistence.markFailed(notification.getId(), ex.getMessage());
            log.error("Pass email delivery failed for event {}", notification.getEventId(), ex);
        }
    }
}
