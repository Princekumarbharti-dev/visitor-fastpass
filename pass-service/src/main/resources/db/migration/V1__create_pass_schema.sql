CREATE TABLE visitor_passes (
    id BIGINT NOT NULL AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    visit_id BIGINT NOT NULL,
    public_reference VARCHAR(24) NOT NULL,
    pass_number VARCHAR(36) NOT NULL,
    qr_token_hash VARCHAR(64) NOT NULL,
    qr_token_ciphertext VARCHAR(512) NOT NULL,
    visitor_name VARCHAR(100) NOT NULL,
    visitor_email VARCHAR(120) NOT NULL,
    host_name VARCHAR(100) NOT NULL,
    purpose VARCHAR(300) NOT NULL,
    scheduled_at DATETIME(6) NOT NULL,
    valid_from DATETIME(6) NOT NULL,
    valid_until DATETIME(6) NOT NULL,
    status VARCHAR(20) NOT NULL,
    generated_at DATETIME(6) NOT NULL,
    used_at DATETIME(6),
    revoked_at DATETIME(6),
    revocation_reason VARCHAR(300),
    CONSTRAINT pk_visitor_passes PRIMARY KEY (id),
    CONSTRAINT uk_passes_visit UNIQUE (visit_id),
    CONSTRAINT uk_passes_number UNIQUE (pass_number),
    CONSTRAINT uk_passes_token_hash UNIQUE (qr_token_hash)
);

CREATE TABLE pass_scans (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pass_id BIGINT,
    token_fingerprint VARCHAR(16) NOT NULL,
    scan_type VARCHAR(20) NOT NULL,
    result VARCHAR(30) NOT NULL,
    scanned_by_user_id BIGINT,
    device_or_location VARCHAR(100),
    failure_reason VARCHAR(300),
    scanned_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_pass_scans PRIMARY KEY (id),
    CONSTRAINT fk_pass_scans_pass FOREIGN KEY (pass_id) REFERENCES visitor_passes (id)
);

CREATE INDEX idx_passes_status_validity ON visitor_passes (status, valid_until);
CREATE INDEX idx_pass_scans_pass_time ON pass_scans (pass_id, scanned_at);
CREATE INDEX idx_pass_scans_fingerprint ON pass_scans (token_fingerprint);
