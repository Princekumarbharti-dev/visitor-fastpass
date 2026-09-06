package com.visitorfastpass.visitor.api.dto;

public record DashboardStatsResponse(
        long totalVisitors,
        long visitorsToday,
        long approvedVisitors,
        long pendingApprovals,
        long checkedInVisitors,
        long completedVisits
) {
}
