CREATE TABLE visitors (
    id BIGINT NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    email VARCHAR(120) NOT NULL,
    organization_name VARCHAR(120) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_visitors PRIMARY KEY (id)
);

CREATE TABLE visits (
    id BIGINT NOT NULL AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    public_reference VARCHAR(24) NOT NULL,
    visitor_id BIGINT NOT NULL,
    host_id BIGINT NOT NULL,
    host_name_snapshot VARCHAR(100) NOT NULL,
    purpose VARCHAR(300) NOT NULL,
    scheduled_at DATETIME(6) NOT NULL,
    status VARCHAR(30) NOT NULL,
    pass_provisioning_status VARCHAR(30) NOT NULL,
    pass_number VARCHAR(30),
    approval_comment VARCHAR(300),
    approved_at DATETIME(6),
    rejected_at DATETIME(6),
    check_in_time DATETIME(6),
    check_out_time DATETIME(6),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_visits PRIMARY KEY (id),
    CONSTRAINT uk_visits_public_reference UNIQUE (public_reference),
    CONSTRAINT fk_visits_visitor FOREIGN KEY (visitor_id) REFERENCES visitors (id)
);

CREATE TABLE visit_events (
    id BIGINT NOT NULL AUTO_INCREMENT,
    visit_id BIGINT NOT NULL,
    event_type VARCHAR(40) NOT NULL,
    performed_by_user_id BIGINT,
    performed_by_name VARCHAR(100) NOT NULL,
    remarks VARCHAR(300),
    event_time DATETIME(6) NOT NULL,
    CONSTRAINT pk_visit_events PRIMARY KEY (id),
    CONSTRAINT fk_visit_events_visit FOREIGN KEY (visit_id) REFERENCES visits (id)
);

CREATE INDEX idx_visits_host_status ON visits (host_id, status);
CREATE INDEX idx_visits_status_scheduled ON visits (status, scheduled_at);
CREATE INDEX idx_visits_created_at ON visits (created_at);
CREATE INDEX idx_visitors_email ON visitors (email);
CREATE INDEX idx_visit_events_visit_time ON visit_events (visit_id, event_time);
