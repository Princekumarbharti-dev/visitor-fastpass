package com.visitorfastpass.visitor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.time.Instant;
import java.time.OffsetDateTime;

@Entity
@Table(name = "visits", uniqueConstraints =
        @UniqueConstraint(name = "uk_visits_public_reference", columnNames = "public_reference"))
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(name = "public_reference", nullable = false, length = 24)
    private String publicReference;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visitor_id", nullable = false)
    private Visitor visitor;

    @Column(name = "host_id", nullable = false)
    private Long hostId;

    @Column(name = "host_name_snapshot", nullable = false, length = 100)
    private String hostNameSnapshot;

    @Column(nullable = false, length = 300)
    private String purpose;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VisitStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "pass_provisioning_status", nullable = false, length = 30)
    private PassProvisioningStatus passProvisioningStatus;

    @Column(name = "pass_number", length = 30)
    private String passNumber;

    @Column(name = "approval_comment", length = 300)
    private String approvalComment;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "rejected_at")
    private Instant rejectedAt;

    @Column(name = "check_in_time")
    private Instant checkInTime;

    @Column(name = "check_out_time")
    private Instant checkOutTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Visit() {}

    public Visit(String publicReference, Visitor visitor, Long hostId, String hostNameSnapshot,
                 String purpose, OffsetDateTime scheduledAt) {
        this.publicReference = publicReference;
        this.visitor = visitor;
        this.hostId = hostId;
        this.hostNameSnapshot = hostNameSnapshot;
        this.purpose = purpose;
        this.scheduledAt = scheduledAt;
        this.status = VisitStatus.PENDING;
        this.passProvisioningStatus = PassProvisioningStatus.NOT_REQUESTED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void approve(String comment) {
        requireStatus(VisitStatus.PENDING, "Only pending visits can be approved");
        this.status = VisitStatus.APPROVED;
        this.passProvisioningStatus = PassProvisioningStatus.PENDING;
        this.approvalComment = clean(comment);
        this.approvedAt = Instant.now();
        this.updatedAt = this.approvedAt;
    }

    public void reject(String reason) {
        requireStatus(VisitStatus.PENDING, "Only pending visits can be rejected");
        this.status = VisitStatus.REJECTED;
        this.approvalComment = reason.trim();
        this.rejectedAt = Instant.now();
        this.updatedAt = this.rejectedAt;
    }

    public void passGenerated(String passNumber) {
        if (status != VisitStatus.APPROVED) {
            throw new IllegalStateException("Pass can be attached only to an approved visit");
        }
        this.passProvisioningStatus = PassProvisioningStatus.GENERATED;
        this.passNumber = passNumber;
        this.updatedAt = Instant.now();
    }

    public void passGenerationFailed() {
        if (status == VisitStatus.APPROVED && passProvisioningStatus == PassProvisioningStatus.PENDING) {
            this.passProvisioningStatus = PassProvisioningStatus.FAILED;
            this.updatedAt = Instant.now();
        }
    }

    public void checkIn() {
        requireStatus(VisitStatus.APPROVED, "Only approved visits can be checked in");
        if (passProvisioningStatus != PassProvisioningStatus.GENERATED) {
            throw new IllegalStateException("A generated pass is required for check-in");
        }
        this.status = VisitStatus.CHECKED_IN;
        this.checkInTime = Instant.now();
        this.updatedAt = this.checkInTime;
    }

    public void checkOut() {
        requireStatus(VisitStatus.CHECKED_IN, "Only checked-in visits can be checked out");
        this.status = VisitStatus.COMPLETED;
        this.checkOutTime = Instant.now();
        this.updatedAt = this.checkOutTime;
    }

    private void requireStatus(VisitStatus required, String message) {
        if (status != required) throw new IllegalStateException(message);
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    public Long getId() { return id; }
    public String getPublicReference() { return publicReference; }
    public Visitor getVisitor() { return visitor; }
    public Long getHostId() { return hostId; }
    public String getHostNameSnapshot() { return hostNameSnapshot; }
    public String getPurpose() { return purpose; }
    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public VisitStatus getStatus() { return status; }
    public PassProvisioningStatus getPassProvisioningStatus() { return passProvisioningStatus; }
    public String getPassNumber() { return passNumber; }
    public String getApprovalComment() { return approvalComment; }
    public Instant getApprovedAt() { return approvedAt; }
    public Instant getRejectedAt() { return rejectedAt; }
    public Instant getCheckInTime() { return checkInTime; }
    public Instant getCheckOutTime() { return checkOutTime; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
