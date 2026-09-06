package com.visitorfastpass.visitor.service;

import com.visitorfastpass.visitor.api.dto.ApprovalRequest;
import com.visitorfastpass.visitor.api.dto.RejectionRequest;
import com.visitorfastpass.visitor.api.dto.VisitSummaryResponse;
import com.visitorfastpass.visitor.client.PassClient.GeneratePassResponse;
import com.visitorfastpass.visitor.security.CurrentActor;
import com.visitorfastpass.visitor.security.CurrentActor.Actor;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService {
    private final VisitCommandService commandService;
    private final PassProvisioningGateway passGateway;
    private final VisitQueryService queryService;
    private final CurrentActor currentActor;

    public ApprovalService(VisitCommandService commandService, PassProvisioningGateway passGateway,
                           VisitQueryService queryService, CurrentActor currentActor) {
        this.commandService = commandService;
        this.passGateway = passGateway;
        this.queryService = queryService;
        this.currentActor = currentActor;
    }

    public VisitSummaryResponse approve(long visitId, ApprovalRequest request) {
        Actor actor = currentActor.get();
        VisitCommandService.ApprovalResult approval = commandService.approve(visitId, request, actor);
        try {
            GeneratePassResponse pass = passGateway.generate(approval.passRequest());
            commandService.markPassGenerated(approval.visitId(), pass.passNumber(), actor);
        } catch (PassProvisioningGateway.PassProvisioningException ex) {
            commandService.markPassFailed(approval.visitId(), actor);
        }
        return queryService.findSummary(visitId);
    }

    public VisitSummaryResponse reject(long visitId, RejectionRequest request) {
        Actor actor = currentActor.get();
        commandService.reject(visitId, request, actor);
        return queryService.findSummary(visitId);
    }
}
