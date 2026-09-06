package com.visitorfastpass.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Table(name = "notifications", uniqueConstraints =
        @UniqueConstraint(name = "uk_notifications_event", columnNames = "event_id"))
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version private long version;

    @Column(name = "event_id", nullable = false, length = 64)
    private String eventId;
    @Column(name = "visit_id", nullable = false)
    private Long visitId;
    @Column(name = "pass_number", nullable = false, length = 36)
    private String passNumber;
    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;
    @Column(name = "recipient_email", nullable = false, length = 120)
    private String recipientEmail;
    @Column(name = "host_name", nullable = false, length = 100)
    private String hostName;
    @Column(nullable = false, length = 300)
    private String purpose;
    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;
    @Column(name = "valid_until", nullable = false)
    private Instant validUntil;
    @Column(name = "digital_pass_url", nullable = false, length = 1000)
    private String digitalPassUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, length = 40)
    private NotificationType notificationType;
    @Column(nullable = false, length = 200)
    private String subject;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationStatus status;
    @Column(name = "retry_count", nullable = false)
    private int retryCount;
    @Column(name = "failure_reason", length = 500)
    private String failureReason;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    @Column(name = "sent_at")
    private Instant sentAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Notification() {}

    public Notification(String eventId, Long visitId, String passNumber, String recipientName,
                        String recipientEmail, String hostName, String purpose, OffsetDateTime scheduledAt,
                        Instant validUntil, String digitalPassUrl, String subject) {
        this.eventId = eventId;
        this.visitId = visitId;
        this.passNumber = passNumber;
        this.recipientName = recipientName;
        this.recipientEmail = recipientEmail;
        this.hostName = hostName;
        this.purpose = purpose;
        this.scheduledAt = scheduledAt;
        this.validUntil = validUntil;
        this.digitalPassUrl = digitalPassUrl;
        this.notificationType = NotificationType.PASS_GENERATED;
        this.subject = subject;
        this.status = NotificationStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void markSent() { status = NotificationStatus.SENT; sentAt = Instant.now(); updatedAt = sentAt; failureReason = null; }
    public void markSimulated() { status = NotificationStatus.SIMULATED; updatedAt = Instant.now(); failureReason = null; }
    public void markFailed(String reason) { status = NotificationStatus.FAILED; retryCount++; failureReason = truncate(reason); updatedAt = Instant.now(); }
    public void prepareRetry() { status = NotificationStatus.PENDING; failureReason = null; updatedAt = Instant.now(); }
    private String truncate(String value) { return value == null ? "Unknown mail error" : value.substring(0, Math.min(500, value.length())); }

    public Long getId() { return id; }
    public String getEventId() { return eventId; }
    public Long getVisitId() { return visitId; }
    public String getPassNumber() { return passNumber; }
    public String getRecipientName() { return recipientName; }
    public String getRecipientEmail() { return recipientEmail; }
    public String getHostName() { return hostName; }
    public String getPurpose() { return purpose; }
    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public Instant getValidUntil() { return validUntil; }
    public String getDigitalPassUrl() { return digitalPassUrl; }
    public NotificationType getNotificationType() { return notificationType; }
    public String getSubject() { return subject; }
    public NotificationStatus getStatus() { return status; }
    public int getRetryCount() { return retryCount; }
    public String getFailureReason() { return failureReason; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
}
