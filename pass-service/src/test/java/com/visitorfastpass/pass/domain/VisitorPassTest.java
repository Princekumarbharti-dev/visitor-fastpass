package com.visitorfastpass.pass.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;

class VisitorPassTest {
    @Test
    void evaluatesValidityAndPreventsReuse() {
        VisitorPass pass = pass(Instant.now().minusSeconds(60), Instant.now().plusSeconds(3600));

        assertThat(pass.evaluateAt(Instant.now())).isEqualTo(ScanResult.VALID);
        pass.markUsed();
        assertThat(pass.evaluateAt(Instant.now())).isEqualTo(ScanResult.ALREADY_USED);
    }

    @Test
    void revokedPassIsNeverValid() {
        VisitorPass pass = pass(Instant.now().minusSeconds(60), Instant.now().plusSeconds(3600));
        pass.revoke("Security request");
        assertThat(pass.evaluateAt(Instant.now())).isEqualTo(ScanResult.REVOKED);
    }

    private VisitorPass pass(Instant from, Instant until) {
        return new VisitorPass(41L, "VF-ABC234567", "FP-20260704-ABC234", "hash", "cipher",
                "Riya Mehta", "riya@example.com", "Asha Sharma", "Meeting",
                OffsetDateTime.now().plusHours(1), from, until);
    }
}
