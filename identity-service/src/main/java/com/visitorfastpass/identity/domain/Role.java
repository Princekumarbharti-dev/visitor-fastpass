package com.visitorfastpass.identity.domain;

import java.util.Set;

public enum Role {
    ADMIN(Set.of(
            "EMPLOYEE_CREATE", "EMPLOYEE_READ", "EMPLOYEE_UPDATE", "EMPLOYEE_DISABLE",
            "VISIT_VIEW_ALL", "VISIT_MANAGE_ALL", "PASS_VERIFY", "PASS_REVOKE",
            "DASHBOARD_VIEW", "REPORT_VIEW", "NOTIFICATION_VIEW_ALL"
    )),
    HOST(Set.of(
            "VISIT_REQUEST_VIEW_OWN", "VISIT_APPROVE_OWN", "VISIT_REJECT_OWN",
            "VISIT_HISTORY_VIEW_OWN", "NOTIFICATION_VIEW_OWN"
    )),
    RECEPTION(Set.of(
            "PASS_VERIFY", "VISITOR_CHECK_IN", "VISITOR_CHECK_OUT",
            "TODAY_VISITS_VIEW", "CURRENT_VISITORS_VIEW"
    ));

    private final Set<String> authorities;

    Role(Set<String> authorities) {
        this.authorities = authorities;
    }

    public Set<String> authorities() {
        return authorities;
    }
}
