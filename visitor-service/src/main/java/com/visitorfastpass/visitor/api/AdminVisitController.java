package com.visitorfastpass.visitor.api;

import com.visitorfastpass.visitor.api.dto.DashboardStatsResponse;
import com.visitorfastpass.visitor.api.dto.PageResponse;
import com.visitorfastpass.visitor.api.dto.VisitDetailsResponse;
import com.visitorfastpass.visitor.api.dto.VisitSummaryResponse;
import com.visitorfastpass.visitor.domain.VisitStatus;
import com.visitorfastpass.visitor.service.VisitQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@Tag(name = "Admin Visits")
@SecurityRequirement(name = "bearerAuth")
@Validated
public class AdminVisitController {
    private final VisitQueryService queryService;

    public AdminVisitController(VisitQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/visits")
    @PreAuthorize("hasAuthority('VISIT_VIEW_ALL')")
    @Operation(summary = "Search all visits using status, text, and pagination")
    public PageResponse<VisitSummaryResponse> search(
            @RequestParam(required = false) VisitStatus status,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
        return queryService.search(status, search, page, size);
    }

    @GetMapping("/visits/{visitId}")
    @PreAuthorize("hasAuthority('VISIT_VIEW_ALL')")
    @Operation(summary = "Get a visit and its complete event timeline")
    public VisitDetailsResponse details(@PathVariable long visitId) {
        return queryService.details(visitId);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    @Operation(summary = "Get visitor dashboard statistics")
    public DashboardStatsResponse dashboard() {
        return queryService.dashboard();
    }
}
