package com.visitorfastpass.notification.service;

import com.visitorfastpass.notification.domain.Notification;
import com.visitorfastpass.notification.event.PassGeneratedEvent;
import com.visitorfastpass.notification.repository.NotificationRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationPersistenceService {
    private final NotificationRepository repository;

    public NotificationPersistenceService(NotificationRepository repository) { this.repository = repository; }

    @Transactional
    public CreationResult createIfAbsent(PassGeneratedEvent event) {
        Optional<Notification> existing = repository.findByEventId(event.eventId());
        if (existing.isPresent()) return new CreationResult(existing.get(), false);
        Notification notification = repository.save(new Notification(
                event.eventId(), event.visitId(), event.passNumber(), event.visitorName(), event.visitorEmail(),
                event.hostName(), event.purpose(), event.scheduledAt(), event.validUntil(), event.digitalPassUrl(),
                "Your Visitor FastPass is approved - " + event.passNumber()));
        return new CreationResult(notification, true);
    }

    @Transactional(readOnly = true)
    public Notification required(long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Notification was not found"));
    }

    @Transactional
    public Notification markSent(long id) { Notification n = required(id); n.markSent(); return n; }
    @Transactional
    public Notification markSimulated(long id) { Notification n = required(id); n.markSimulated(); return n; }
    @Transactional
    public Notification markFailed(long id, String reason) { Notification n = required(id); n.markFailed(reason); return n; }
    @Transactional
    public Notification prepareRetry(long id) { Notification n = required(id); n.prepareRetry(); return n; }

    public record CreationResult(Notification notification, boolean created) {}
}
