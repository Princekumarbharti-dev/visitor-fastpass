package com.visitorfastpass.visitor.service;

import com.visitorfastpass.visitor.api.dto.VisitEventResponse;
import com.visitorfastpass.visitor.api.dto.VisitSummaryResponse;
import com.visitorfastpass.visitor.domain.Visit;
import com.visitorfastpass.visitor.domain.VisitEvent;
import com.visitorfastpass.visitor.domain.Visitor;
import org.springframework.stereotype.Component;

@Component
public class VisitMapper {
    public VisitSummaryResponse toSummary(Visit visit) {
        Visitor visitor = visit.getVisitor();
        return new VisitSummaryResponse(
                visit.getId(), visit.getPublicReference(), visitor.getFullName(), visitor.getEmail(),
                visitor.getMobileNumber(), visitor.getOrganizationName(), visit.getHostId(),
                visit.getHostNameSnapshot(), visit.getPurpose(), visit.getScheduledAt(), visit.getStatus(),
                visit.getPassProvisioningStatus(), visit.getPassNumber(), visit.getCheckInTime(),
                visit.getCheckOutTime(), visit.getCreatedAt());
    }

    public VisitEventResponse toEvent(VisitEvent event) {
        return new VisitEventResponse(event.getEventType(), event.getPerformedByUserId(),
                event.getPerformedByName(), event.getRemarks(), event.getEventTime());
    }
}
