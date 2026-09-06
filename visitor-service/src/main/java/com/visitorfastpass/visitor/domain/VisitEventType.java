package com.visitorfastpass.visitor.domain;

public enum VisitEventType {
    REGISTERED,
    APPROVED,
    REJECTED,
    PASS_GENERATED,
    PASS_GENERATION_FAILED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED,
    EXPIRED
}
