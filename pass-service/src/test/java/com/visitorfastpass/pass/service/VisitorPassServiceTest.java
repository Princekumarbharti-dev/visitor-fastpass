package com.visitorfastpass.pass.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.visitorfastpass.pass.api.dto.GeneratePassRequest;
import com.visitorfastpass.pass.config.PassProperties;
import com.visitorfastpass.pass.domain.PassScan;
import com.visitorfastpass.pass.domain.VisitorPass;
import com.visitorfastpass.pass.repository.PassScanRepository;
import com.visitorfastpass.pass.repository.VisitorPassRepository;
import com.visitorfastpass.pass.security.TokenCrypto;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class VisitorPassServiceTest {
    @Mock VisitorPassRepository passRepository;
    @Mock PassScanRepository scanRepository;
    @Mock PassNumberGenerator numberGenerator;
    @Mock ApplicationEventPublisher eventPublisher;

    private VisitorPassService service;
    private TokenCrypto crypto;

    @BeforeEach
    void setUp() {
        var properties = new PassProperties("test-pass-encryption-secret-at-least-32-characters",
                "http://localhost:5173", 120, 480);
        crypto = new TokenCrypto(properties);
        service = new VisitorPassService(passRepository, scanRepository, numberGenerator, crypto, properties, eventPublisher);
    }

    @Test
    void generationIsIdempotentAndReturnsSameSecureUrl() {
        AtomicReference<VisitorPass> stored = new AtomicReference<>();
        when(passRepository.findByVisitId(41L)).thenAnswer(invocation -> Optional.ofNullable(stored.get()));
        when(numberGenerator.next()).thenReturn("FP-20260704-ABC234");
        when(passRepository.save(any(VisitorPass.class))).thenAnswer(invocation -> {
            VisitorPass pass = invocation.getArgument(0);
            ReflectionTestUtils.setField(pass, "id", 9L);
            stored.set(pass);
            return pass;
        });

        var first = service.generate(request());
        var retry = service.generate(request());

        assertThat(retry.passId()).isEqualTo(first.passId());
        assertThat(retry.passNumber()).isEqualTo(first.passNumber());
        assertThat(retry.digitalPassUrl()).isEqualTo(first.digitalPassUrl());
        assertThat(first.digitalPassUrl()).doesNotContain("riya@example.com");
    }

    @Test
    void unknownTokenProducesAuditedNotFoundResult() {
        when(passRepository.findByQrTokenHash(any())).thenReturn(Optional.empty());
        when(scanRepository.save(any(PassScan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var result = service.verify("unknown-secure-token", "Front desk", 3L);

        assertThat(result.valid()).isFalse();
        assertThat(result.visitId()).isNull();
        assertThat(result.result().name()).isEqualTo("NOT_FOUND");
    }

    private GeneratePassRequest request() {
        return new GeneratePassRequest(41L, "VF-ABC234567", "Riya Mehta", "riya@example.com",
                "Asha Sharma", "Product demonstration", OffsetDateTime.now().plusHours(2));
    }
}
