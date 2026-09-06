package com.visitorfastpass.visitor.api.dto;

import java.util.List;

public record VisitDetailsResponse(
        VisitSummaryResponse visit,
        String approvalComment,
        List<VisitEventResponse> events
) {
}
