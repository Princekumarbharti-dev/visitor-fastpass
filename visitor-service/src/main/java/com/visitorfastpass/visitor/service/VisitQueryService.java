package com.visitorfastpass.visitor.service;

import com.visitorfastpass.visitor.api.dto.DashboardStatsResponse;
import com.visitorfastpass.visitor.api.dto.PageResponse;
import com.visitorfastpass.visitor.api.dto.VisitDetailsResponse;
import com.visitorfastpass.visitor.api.dto.VisitStatusResponse;
import com.visitorfastpass.visitor.api.dto.VisitSummaryResponse;
import com.visitorfastpass.visitor.domain.Visit;
import com.visitorfastpass.visitor.domain.VisitStatus;
import com.visitorfastpass.visitor.domain.Visitor;
import com.visitorfastpass.visitor.exception.ApiException;
import com.visitorfastpass.visitor.repository.VisitEventRepository;
import com.visitorfastpass.visitor.repository.VisitRepository;
import com.visitorfastpass.visitor.security.CurrentActor.Actor;
import jakarta.persistence.criteria.Join;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VisitQueryService {
    private final VisitRepository visitRepository;
    private final VisitEventRepository eventRepository;
    private final VisitMapper mapper;

    public VisitQueryService(VisitRepository visitRepository, VisitEventRepository eventRepository,
                             VisitMapper mapper) {
        this.visitRepository = visitRepository;
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public VisitStatusResponse publicStatus(String reference) {
        Visit visit = requiredByReference(reference.trim().toUpperCase());
        return new VisitStatusResponse(visit.getPublicReference(), visit.getVisitor().getFullName(),
                visit.getHostNameSnapshot(), visit.getScheduledAt(), visit.getStatus(),
                visit.getPassProvisioningStatus(), visit.getPassNumber(), visit.getApprovalComment(),
                visit.getCheckInTime(), visit.getCheckOutTime());
    }

    @Transactional(readOnly = true)
    public List<VisitSummaryResponse> pendingForHost(Actor actor) {
        requireHostIdentity(actor);
        return visitRepository.findByHostIdAndStatusOrderByScheduledAtAsc(
                        actor.employeeId(), VisitStatus.PENDING).stream()
                .map(mapper::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<VisitSummaryResponse> historyForHost(Actor actor) {
        requireHostIdentity(actor);
        return visitRepository.findByHostIdAndStatusNotOrderByScheduledAtDesc(
                        actor.employeeId(), VisitStatus.PENDING).stream()
                .map(mapper::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public VisitSummaryResponse findSummary(long visitId) {
        return mapper.toSummary(requiredVisit(visitId));
    }

    @Transactional(readOnly = true)
    public VisitDetailsResponse details(long visitId) {
        Visit visit = requiredVisit(visitId);
        var events = eventRepository.findByVisitIdOrderByEventTimeAsc(visitId).stream()
                .map(mapper::toEvent).toList();
        return new VisitDetailsResponse(mapper.toSummary(visit), visit.getApprovalComment(), events);
    }

    @Transactional(readOnly = true)
    public PageResponse<VisitSummaryResponse> search(VisitStatus status, String search, int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Specification<Visit> specification = (root, query, cb) -> cb.conjunction();
        if (status != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (search != null && !search.isBlank()) {
            String term = "%" + search.trim().toLowerCase() + "%";
            specification = specification.and((root, query, cb) -> {
                Join<Visit, Visitor> visitor = root.join("visitor");
                return cb.or(
                        cb.like(cb.lower(visitor.get("fullName")), term),
                        cb.like(cb.lower(visitor.get("email")), term),
                        cb.like(cb.lower(visitor.get("mobileNumber")), term),
                        cb.like(cb.lower(root.get("publicReference")), term));
            });
        }
        Page<VisitSummaryResponse> result = visitRepository.findAll(specification,
                        PageRequest.of(Math.max(page, 0), safeSize, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(mapper::toSummary);
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public DashboardStatsResponse dashboard() {
        Instant today = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant tomorrow = today.plusSeconds(86_400);
        return new DashboardStatsResponse(
                visitRepository.count(),
                visitRepository.countByCreatedAtBetween(today, tomorrow),
                visitRepository.countByStatus(VisitStatus.APPROVED),
                visitRepository.countByStatus(VisitStatus.PENDING),
                visitRepository.countByStatus(VisitStatus.CHECKED_IN),
                visitRepository.countByStatus(VisitStatus.COMPLETED));
    }

    private Visit requiredByReference(String reference) {
        return visitRepository.findByPublicReference(reference)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "VISIT_NOT_FOUND", "No visit was found for this reference"));
    }

    private Visit requiredVisit(long visitId) {
        return visitRepository.findById(visitId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "VISIT_NOT_FOUND", "Visit was not found"));
    }

    private void requireHostIdentity(Actor actor) {
        if (actor.employeeId() == null) {
            throw new ApiException(HttpStatus.FORBIDDEN, "HOST_IDENTITY_REQUIRED",
                    "The token does not contain an employee identity");
        }
    }
}
