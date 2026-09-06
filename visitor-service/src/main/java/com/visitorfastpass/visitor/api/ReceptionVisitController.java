package com.visitorfastpass.visitor.api;

import com.visitorfastpass.visitor.api.dto.CheckInRequest;
import com.visitorfastpass.visitor.api.dto.VisitSummaryResponse;
import com.visitorfastpass.visitor.service.ReceptionVisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reception/visits")
@Tag(name = "Reception Visits")
@SecurityRequirement(name = "bearerAuth")
public class ReceptionVisitController {
    private final ReceptionVisitService receptionVisitService;

    public ReceptionVisitController(ReceptionVisitService receptionVisitService) {
        this.receptionVisitService = receptionVisitService;
    }

    @PostMapping("/check-in")
    @PreAuthorize("hasAuthority('VISITOR_CHECK_IN')")
    @Operation(summary = "Verify and consume a QR pass, then check the visitor in")
    public VisitSummaryResponse checkIn(@Valid @RequestBody CheckInRequest request) {
        return receptionVisitService.checkIn(request);
    }

    @PostMapping("/{visitId}/check-out")
    @PreAuthorize("hasAuthority('VISITOR_CHECK_OUT')")
    @Operation(summary = "Check out a currently checked-in visitor")
    public VisitSummaryResponse checkOut(@PathVariable long visitId) {
        return receptionVisitService.checkOut(visitId);
    }
}
