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
import java.time.Instant;

@Entity
@Table(name = "visit_events")
public class VisitEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "visit_id", nullable = false)
    private Visit visit;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private VisitEventType eventType;

    @Column(name = "performed_by_user_id")
    private Long performedByUserId;

    @Column(name = "performed_by_name", nullable = false, length = 100)
    private String performedByName;

    @Column(length = 300)
    private String remarks;

    @Column(name = "event_time", nullable = false, updatable = false)
    private Instant eventTime;

    protected VisitEvent() {}

    public VisitEvent(Visit visit, VisitEventType eventType, Long performedByUserId,
                      String performedByName, String remarks) {
        this.visit = visit;
        this.eventType = eventType;
        this.performedByUserId = performedByUserId;
        this.performedByName = performedByName;
        this.remarks = remarks;
        this.eventTime = Instant.now();
    }

    public Long getId() { return id; }
    public Visit getVisit() { return visit; }
    public VisitEventType getEventType() { return eventType; }
    public Long getPerformedByUserId() { return performedByUserId; }
    public String getPerformedByName() { return performedByName; }
    public String getRemarks() { return remarks; }
    public Instant getEventTime() { return eventTime; }
}
