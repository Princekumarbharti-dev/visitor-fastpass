package com.visitorfastpass.visitor.api;

import com.visitorfastpass.visitor.api.dto.ApprovalRequest;
import com.visitorfastpass.visitor.api.dto.RejectionRequest;
import com.visitorfastpass.visitor.api.dto.VisitSummaryResponse;
import com.visitorfastpass.visitor.security.CurrentActor;
import com.visitorfastpass.visitor.service.ApprovalService;
import com.visitorfastpass.visitor.service.VisitQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/host/visits")
@Tag(name = "Host Visits")
@SecurityRequirement(name = "bearerAuth")
public class HostVisitController {
    private final ApprovalService approvalService;
    private final VisitQueryService queryService;
    private final CurrentActor currentActor;

    public HostVisitController(ApprovalService approvalService, VisitQueryService queryService,
                               CurrentActor currentActor) {
        this.approvalService = approvalService;
        this.queryService = queryService;
        this.currentActor = currentActor;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('VISIT_REQUEST_VIEW_OWN')")
    @Operation(summary = "List pending visits assigned to the authenticated host")
    public List<VisitSummaryResponse> pending() {
        return queryService.pendingForHost(currentActor.get());
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('VISIT_HISTORY_VIEW_OWN')")
    @Operation(summary = "List decision history for the authenticated host")
    public List<VisitSummaryResponse> history() {
        return queryService.historyForHost(currentActor.get());
    }

    @PatchMapping("/{visitId}/approve")
    @PreAuthorize("hasAuthority('VISIT_APPROVE_OWN') or hasAuthority('VISIT_MANAGE_ALL')")
    @Operation(summary = "Approve an assigned pending visit and request pass generation")
    public VisitSummaryResponse approve(@PathVariable long visitId,
                                        @Valid @RequestBody ApprovalRequest request) {
        return approvalService.approve(visitId, request);
    }

    @PatchMapping("/{visitId}/reject")
    @PreAuthorize("hasAuthority('VISIT_REJECT_OWN') or hasAuthority('VISIT_MANAGE_ALL')")
    @Operation(summary = "Reject an assigned pending visit")
    public VisitSummaryResponse reject(@PathVariable long visitId,
                                       @Valid @RequestBody RejectionRequest request) {
        return approvalService.reject(visitId, request);
    }
}
