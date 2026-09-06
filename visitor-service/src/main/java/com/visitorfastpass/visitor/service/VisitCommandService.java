package com.visitorfastpass.visitor.service;

import com.visitorfastpass.visitor.api.dto.ApprovalRequest;
import com.visitorfastpass.visitor.api.dto.RejectionRequest;
import com.visitorfastpass.visitor.api.dto.VisitorRegistrationRequest;
import com.visitorfastpass.visitor.api.dto.VisitorRegistrationResponse;
import com.visitorfastpass.visitor.client.IdentityClient.HostResponse;
import com.visitorfastpass.visitor.client.PassClient.GeneratePassRequest;
import com.visitorfastpass.visitor.domain.Visit;
import com.visitorfastpass.visitor.domain.VisitEvent;
import com.visitorfastpass.visitor.domain.VisitEventType;
import com.visitorfastpass.visitor.domain.Visitor;
import com.visitorfastpass.visitor.exception.ApiException;
import com.visitorfastpass.visitor.repository.VisitEventRepository;
import com.visitorfastpass.visitor.repository.VisitRepository;
import com.visitorfastpass.visitor.repository.VisitorRepository;
import com.visitorfastpass.visitor.security.CurrentActor.Actor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitCommandService {
    private final VisitorRepository visitorRepository;
    private final VisitRepository visitRepository;
    private final VisitEventRepository eventRepository;
    private final PublicReferenceGenerator referenceGenerator;

    public VisitCommandService(VisitorRepository visitorRepository, VisitRepository visitRepository,
                               VisitEventRepository eventRepository, PublicReferenceGenerator referenceGenerator) {
        this.visitorRepository = visitorRepository;
        this.visitRepository = visitRepository;
        this.eventRepository = eventRepository;
        this.referenceGenerator = referenceGenerator;
    }

    @Transactional
    public VisitorRegistrationResponse register(VisitorRegistrationRequest request, HostResponse host) {
        // Product decision: every registration creates a new Visitor row.
        Visitor visitor = visitorRepository.save(new Visitor(
                clean(request.fullName()), clean(request.mobileNumber()), request.email().trim().toLowerCase(),
                clean(request.organizationName())));
        Visit visit = visitRepository.save(new Visit(
                referenceGenerator.next(), visitor, host.id(), host.fullName(),
                clean(request.purpose()), request.scheduledAt()));
        eventRepository.save(new VisitEvent(visit, VisitEventType.REGISTERED, null,
                "VISITOR", "Visit request submitted"));
        return new VisitorRegistrationResponse(visit.getId(), visit.getPublicReference(), visit.getStatus(),
                visit.getHostNameSnapshot(), visit.getScheduledAt(),
                "Visit request submitted for host approval");
    }

    @Transactional
    public ApprovalResult approve(long visitId, ApprovalRequest request, Actor actor) {
        Visit visit = ownedPendingVisit(visitId, actor);
        try {
            visit.approve(request.comment());
        } catch (IllegalStateException ex) {
            throw invalidState(ex.getMessage());
        }
        eventRepository.save(new VisitEvent(visit, VisitEventType.APPROVED,
                actor.userId(), actor.displayName(), request.comment()));
        Visitor visitor = visit.getVisitor();
        return new ApprovalResult(visit.getId(), new GeneratePassRequest(
                visit.getId(), visit.getPublicReference(), visitor.getFullName(), visitor.getEmail(),
                visit.getHostNameSnapshot(), visit.getPurpose(), visit.getScheduledAt()));
    }

    @Transactional
    public void markPassGenerated(long visitId, String passNumber, Actor actor) {
        Visit visit = requiredVisit(visitId);
        try {
            visit.passGenerated(passNumber);
        } catch (IllegalStateException ex) {
            throw invalidState(ex.getMessage());
        }
        eventRepository.save(new VisitEvent(visit, VisitEventType.PASS_GENERATED,
                actor.userId(), actor.displayName(), "Pass " + passNumber + " generated"));
    }

    @Transactional
    public void markPassFailed(long visitId, Actor actor) {
        Visit visit = requiredVisit(visitId);
        visit.passGenerationFailed();
        eventRepository.save(new VisitEvent(visit, VisitEventType.PASS_GENERATION_FAILED,
                actor.userId(), actor.displayName(), "Pass generation will be retried"));
    }

    @Transactional
    public void reject(long visitId, RejectionRequest request, Actor actor) {
        Visit visit = ownedPendingVisit(visitId, actor);
        try {
            visit.reject(request.reason());
        } catch (IllegalStateException ex) {
            throw invalidState(ex.getMessage());
        }
        eventRepository.save(new VisitEvent(visit, VisitEventType.REJECTED,
                actor.userId(), actor.displayName(), request.reason().trim()));
    }

    @Transactional
    public void checkIn(long visitId, Actor actor) {
        Visit visit = requiredVisit(visitId);
        try {
            visit.checkIn();
        } catch (IllegalStateException ex) {
            throw invalidState(ex.getMessage());
        }
        eventRepository.save(new VisitEvent(visit, VisitEventType.CHECKED_IN,
                actor.userId(), actor.displayName(), "Visitor checked in"));
    }

    @Transactional
    public void checkOut(long visitId, Actor actor) {
        Visit visit = requiredVisit(visitId);
        try {
            visit.checkOut();
        } catch (IllegalStateException ex) {
            throw invalidState(ex.getMessage());
        }
        eventRepository.save(new VisitEvent(visit, VisitEventType.CHECKED_OUT,
                actor.userId(), actor.displayName(), "Visitor checked out"));
    }

    private Visit ownedPendingVisit(long visitId, Actor actor) {
        Visit visit = requiredVisit(visitId);
        if (!actor.isAdmin() && (actor.employeeId() == null || !visit.getHostId().equals(actor.employeeId()))) {
            throw new ApiException(HttpStatus.FORBIDDEN, "VISIT_NOT_ASSIGNED",
                    "This visit request is assigned to another host");
        }
        return visit;
    }

    private Visit requiredVisit(long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "VISIT_NOT_FOUND", "Visit was not found"));
    }

    private ApiException invalidState(String message) {
        return new ApiException(HttpStatus.CONFLICT, "INVALID_VISIT_STATE", message);
    }

    private String clean(String value) { return value.trim().replaceAll("\\s+", " "); }

    public record ApprovalResult(long visitId, GeneratePassRequest passRequest) {}
}
