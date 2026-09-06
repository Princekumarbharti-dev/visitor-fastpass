package com.visitorfastpass.pass.domain;

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
import java.time.Instant;

@Entity
@Table(name = "pass_scans")
public class PassScan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pass_id")
    private VisitorPass pass;

    @Column(name = "token_fingerprint", nullable = false, length = 16)
    private String tokenFingerprint;

    @Enumerated(EnumType.STRING)
    @Column(name = "scan_type", nullable = false, length = 20)
    private ScanType scanType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ScanResult result;

    @Column(name = "scanned_by_user_id")
    private Long scannedByUserId;

    @Column(name = "device_or_location", length = 100)
    private String deviceOrLocation;

    @Column(name = "failure_reason", length = 300)
    private String failureReason;

    @Column(name = "scanned_at", nullable = false, updatable = false)
    private Instant scannedAt;

    protected PassScan() {}

    public PassScan(VisitorPass pass, String tokenFingerprint, ScanType scanType, ScanResult result,
                    Long scannedByUserId, String deviceOrLocation, String failureReason) {
        this.pass = pass;
        this.tokenFingerprint = tokenFingerprint;
        this.scanType = scanType;
        this.result = result;
        this.scannedByUserId = scannedByUserId;
        this.deviceOrLocation = deviceOrLocation;
        this.failureReason = failureReason;
        this.scannedAt = Instant.now();
    }

    public Long getId() { return id; }
    public VisitorPass getPass() { return pass; }
    public String getTokenFingerprint() { return tokenFingerprint; }
    public ScanType getScanType() { return scanType; }
    public ScanResult getResult() { return result; }
    public Long getScannedByUserId() { return scannedByUserId; }
    public String getDeviceOrLocation() { return deviceOrLocation; }
    public String getFailureReason() { return failureReason; }
    public Instant getScannedAt() { return scannedAt; }
}
