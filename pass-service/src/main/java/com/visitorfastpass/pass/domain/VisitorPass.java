package com.visitorfastpass.pass.domain;

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
@Table(name = "visitor_passes", uniqueConstraints = {
        @UniqueConstraint(name = "uk_passes_visit", columnNames = "visit_id"),
        @UniqueConstraint(name = "uk_passes_number", columnNames = "pass_number"),
        @UniqueConstraint(name = "uk_passes_token_hash", columnNames = "qr_token_hash")
})
public class VisitorPass {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private long version;

    @Column(name = "visit_id", nullable = false)
    private Long visitId;

    @Column(name = "public_reference", nullable = false, length = 24)
    private String publicReference;

    @Column(name = "pass_number", nullable = false, length = 36)
    private String passNumber;

    @Column(name = "qr_token_hash", nullable = false, length = 64)
    private String qrTokenHash;

    @Column(name = "qr_token_ciphertext", nullable = false, length = 512)
    private String qrTokenCiphertext;

    @Column(name = "visitor_name", nullable = false, length = 100)
    private String visitorName;

    @Column(name = "visitor_email", nullable = false, length = 120)
    private String visitorEmail;

    @Column(name = "host_name", nullable = false, length = 100)
    private String hostName;

    @Column(nullable = false, length = 300)
    private String purpose;

    @Column(name = "scheduled_at", nullable = false)
    private OffsetDateTime scheduledAt;

    @Column(name = "valid_from", nullable = false)
    private Instant validFrom;

    @Column(name = "valid_until", nullable = false)
    private Instant validUntil;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PassStatus status;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private Instant generatedAt;

    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "revocation_reason", length = 300)
    private String revocationReason;

    protected VisitorPass() {}

    public VisitorPass(Long visitId, String publicReference, String passNumber, String qrTokenHash,
                       String qrTokenCiphertext, String visitorName, String visitorEmail,
                       String hostName, String purpose, OffsetDateTime scheduledAt,
                       Instant validFrom, Instant validUntil) {
        this.visitId = visitId;
        this.publicReference = publicReference;
        this.passNumber = passNumber;
        this.qrTokenHash = qrTokenHash;
        this.qrTokenCiphertext = qrTokenCiphertext;
        this.visitorName = visitorName;
        this.visitorEmail = visitorEmail;
        this.hostName = hostName;
        this.purpose = purpose;
        this.scheduledAt = scheduledAt;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.status = PassStatus.ACTIVE;
        this.generatedAt = Instant.now();
    }

    public ScanResult evaluateAt(Instant now) {
        if (status == PassStatus.REVOKED) return ScanResult.REVOKED;
        if (status == PassStatus.USED) return ScanResult.ALREADY_USED;
        if (now.isBefore(validFrom)) return ScanResult.NOT_YET_VALID;
        if (now.isAfter(validUntil) || status == PassStatus.EXPIRED) return ScanResult.EXPIRED;
        return ScanResult.VALID;
    }

    public void markUsed() {
        if (evaluateAt(Instant.now()) != ScanResult.VALID) {
            throw new IllegalStateException("Only a currently valid pass can be marked used");
        }
        status = PassStatus.USED;
        usedAt = Instant.now();
    }

    public void revoke(String reason) {
        if (status == PassStatus.REVOKED) throw new IllegalStateException("Pass is already revoked");
        status = PassStatus.REVOKED;
        revokedAt = Instant.now();
        revocationReason = reason.trim();
    }

    public Long getId() { return id; }
    public Long getVisitId() { return visitId; }
    public String getPublicReference() { return publicReference; }
    public String getPassNumber() { return passNumber; }
    public String getQrTokenHash() { return qrTokenHash; }
    public String getQrTokenCiphertext() { return qrTokenCiphertext; }
    public String getVisitorName() { return visitorName; }
    public String getVisitorEmail() { return visitorEmail; }
    public String getHostName() { return hostName; }
    public String getPurpose() { return purpose; }
    public OffsetDateTime getScheduledAt() { return scheduledAt; }
    public Instant getValidFrom() { return validFrom; }
    public Instant getValidUntil() { return validUntil; }
    public PassStatus getStatus() { return status; }
    public Instant getGeneratedAt() { return generatedAt; }
    public Instant getUsedAt() { return usedAt; }
    public Instant getRevokedAt() { return revokedAt; }
    public String getRevocationReason() { return revocationReason; }
}
