package com.visitorfastpass.visitor.service;

import com.visitorfastpass.visitor.api.dto.CheckInRequest;
import com.visitorfastpass.visitor.api.dto.VisitSummaryResponse;
import com.visitorfastpass.visitor.exception.ApiException;
import com.visitorfastpass.visitor.security.CurrentActor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReceptionVisitService {
    private final PassProvisioningGateway passGateway;
    private final VisitCommandService commandService;
    private final VisitQueryService queryService;
    private final CurrentActor currentActor;

    public ReceptionVisitService(PassProvisioningGateway passGateway, VisitCommandService commandService,
                                 VisitQueryService queryService, CurrentActor currentActor) {
        this.passGateway = passGateway;
        this.commandService = commandService;
        this.queryService = queryService;
        this.currentActor = currentActor;
    }

    public VisitSummaryResponse checkIn(CheckInRequest request) {
        var verification = passGateway.verify(request.token(), request.location());
        if (!verification.valid() || verification.visitId() == null) {
            throw new ApiException(HttpStatus.CONFLICT, "PASS_NOT_VALID",
                    "Visitor pass cannot be used: " + verification.result());
        }
        // Consume first so a partial failure cannot leave a reusable gate credential.
        passGateway.markUsed(verification.visitId(), request.token());
        commandService.checkIn(verification.visitId(), currentActor.get());
        return queryService.findSummary(verification.visitId());
    }

    public VisitSummaryResponse checkOut(long visitId) {
        commandService.checkOut(visitId, currentActor.get());
        return queryService.findSummary(visitId);
    }
}
