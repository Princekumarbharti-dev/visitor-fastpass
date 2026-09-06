CREATE TABLE notifications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    event_id VARCHAR(64) NOT NULL,
    visit_id BIGINT NOT NULL,
    pass_number VARCHAR(36) NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    recipient_email VARCHAR(120) NOT NULL,
    host_name VARCHAR(100) NOT NULL,
    purpose VARCHAR(300) NOT NULL,
    scheduled_at DATETIME(6) NOT NULL,
    valid_until DATETIME(6) NOT NULL,
    digital_pass_url VARCHAR(1000) NOT NULL,
    notification_type VARCHAR(40) NOT NULL,
    subject VARCHAR(200) NOT NULL,
    status VARCHAR(20) NOT NULL,
    retry_count INT NOT NULL DEFAULT 0,
    failure_reason VARCHAR(500),
    created_at DATETIME(6) NOT NULL,
    sent_at DATETIME(6),
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT uk_notifications_event UNIQUE (event_id)
);

CREATE INDEX idx_notifications_status_created ON notifications (status, created_at);
CREATE INDEX idx_notifications_recipient ON notifications (recipient_email, created_at);
