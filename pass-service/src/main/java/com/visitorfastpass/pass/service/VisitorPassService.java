package com.visitorfastpass.pass.service;

import com.visitorfastpass.pass.api.dto.GeneratePassRequest;
import com.visitorfastpass.pass.api.dto.GeneratePassResponse;
import com.visitorfastpass.pass.api.dto.PublicPassResponse;
import com.visitorfastpass.pass.api.dto.VerifyPassResponse;
import com.visitorfastpass.pass.config.PassProperties;
import com.visitorfastpass.pass.domain.PassScan;
import com.visitorfastpass.pass.domain.ScanResult;
import com.visitorfastpass.pass.domain.ScanType;
import com.visitorfastpass.pass.domain.VisitorPass;
import com.visitorfastpass.pass.exception.ApiException;
import com.visitorfastpass.pass.event.PassGeneratedEvent;
import com.visitorfastpass.pass.repository.PassScanRepository;
import com.visitorfastpass.pass.repository.VisitorPassRepository;
import com.visitorfastpass.pass.security.TokenCrypto;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class VisitorPassService {
    private final VisitorPassRepository passRepository;
    private final PassScanRepository scanRepository;
    private final PassNumberGenerator numberGenerator;
    private final TokenCrypto tokenCrypto;
    private final PassProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    public VisitorPassService(VisitorPassRepository passRepository, PassScanRepository scanRepository,
                              PassNumberGenerator numberGenerator, TokenCrypto tokenCrypto,
                              PassProperties properties, ApplicationEventPublisher eventPublisher) {
        this.passRepository = passRepository;
        this.scanRepository = scanRepository;
        this.numberGenerator = numberGenerator;
        this.tokenCrypto = tokenCrypto;
        this.properties = properties;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public GeneratePassResponse generate(GeneratePassRequest request) {
        return passRepository.findByVisitId(request.visitId())
                .map(this::toGenerationResponse)
                .orElseGet(() -> create(request));
    }

    @Transactional
    public VerifyPassResponse verify(String rawToken, String location, Long userId) {
        String hash = tokenCrypto.hash(rawToken.trim());
        VisitorPass pass = passRepository.findByQrTokenHash(hash).orElse(null);
        ScanResult result = pass == null ? ScanResult.NOT_FOUND : pass.evaluateAt(Instant.now());
        scanRepository.save(new PassScan(pass, fingerprint(hash), ScanType.VERIFY, result, userId,
                clean(location), result == ScanResult.VALID ? null : result.name()));
        if (pass == null) return invalid(result);
        return new VerifyPassResponse(result == ScanResult.VALID, result, pass.getVisitId(),
                pass.getPassNumber(), pass.getVisitorName(), pass.getHostName(), pass.getPurpose(),
                pass.getScheduledAt(), pass.getValidUntil(), pass.getStatus(),
                result == ScanResult.VALID ? "CHECK_IN" : "NONE");
    }

    @Transactional(readOnly = true)
    public PublicPassResponse publicPass(String passNumber, String rawToken) {
        VisitorPass pass = authorizedPass(passNumber, rawToken);
        return new PublicPassResponse(pass.getPassNumber(), pass.getVisitorName(), pass.getHostName(),
                pass.getPurpose(), pass.getScheduledAt(), pass.getValidFrom(), pass.getValidUntil(), pass.getStatus());
    }

    @Transactional(readOnly = true)
    public String qrValue(String passNumber, String rawToken) {
        authorizedPass(passNumber, rawToken);
        return digitalPassUrl(passNumber, rawToken);
    }

    @Transactional
    public PublicPassResponse markUsed(long visitId, String rawToken, Long userId) {
        VisitorPass pass = passRepository.findByVisitId(visitId)
                .orElseThrow(() -> notFound("Pass was not found for this visit"));
        requireMatchingToken(pass, rawToken);
        ScanResult current = pass.evaluateAt(Instant.now());
        if (current != ScanResult.VALID) {
            scanRepository.save(new PassScan(pass, fingerprint(pass.getQrTokenHash()), ScanType.CHECK_IN,
                    current, userId, null, current.name()));
            throw new ApiException(HttpStatus.CONFLICT, "PASS_NOT_USABLE", "Pass cannot be used: " + current);
        }
        pass.markUsed();
        scanRepository.save(new PassScan(pass, fingerprint(pass.getQrTokenHash()), ScanType.CHECK_IN,
                ScanResult.VALID, userId, null, null));
        return toPublicResponse(pass);
    }

    @Transactional
    public PublicPassResponse revoke(long passId, String reason) {
        VisitorPass pass = passRepository.findById(passId)
                .orElseThrow(() -> notFound("Pass was not found"));
        try {
            pass.revoke(reason);
        } catch (IllegalStateException ex) {
            throw new ApiException(HttpStatus.CONFLICT, "INVALID_PASS_STATE", ex.getMessage());
        }
        return toPublicResponse(pass);
    }

    private GeneratePassResponse create(GeneratePassRequest request) {
        String rawToken = tokenCrypto.newToken();
        Instant scheduled = request.scheduledAt().toInstant();
        VisitorPass pass = passRepository.save(new VisitorPass(
                request.visitId(), request.publicReference().trim().toUpperCase(), numberGenerator.next(),
                tokenCrypto.hash(rawToken), tokenCrypto.encrypt(rawToken), request.visitorName().trim(),
                request.visitorEmail().trim().toLowerCase(), request.hostName().trim(), request.purpose().trim(),
                request.scheduledAt(), scheduled.minusSeconds(properties.validBeforeMinutes() * 60),
                scheduled.plusSeconds(properties.validAfterMinutes() * 60)));
        GeneratePassResponse response = response(pass, rawToken);
        eventPublisher.publishEvent(new PassGeneratedEvent(
                UUID.randomUUID().toString(), "PASS_GENERATED", Instant.now(), pass.getVisitId(),
                pass.getPublicReference(), pass.getPassNumber(), response.digitalPassUrl(),
                pass.getVisitorName(), pass.getVisitorEmail(), pass.getHostName(), pass.getPurpose(),
                pass.getScheduledAt(), pass.getValidUntil()));
        return response;
    }

    private GeneratePassResponse toGenerationResponse(VisitorPass pass) {
        return response(pass, tokenCrypto.decrypt(pass.getQrTokenCiphertext()));
    }

    private GeneratePassResponse response(VisitorPass pass, String rawToken) {
        return new GeneratePassResponse(pass.getId(), pass.getPassNumber(), pass.getStatus(),
                digitalPassUrl(pass.getPassNumber(), rawToken), pass.getValidFrom(), pass.getValidUntil());
    }

    private String digitalPassUrl(String passNumber, String rawToken) {
        return UriComponentsBuilder.fromUriString(properties.publicBaseUrl())
                .pathSegment("pass", passNumber)
                .queryParam("token", rawToken)
                .build().encode().toUriString();
    }

    private VisitorPass authorizedPass(String passNumber, String rawToken) {
        VisitorPass pass = passRepository.findByPassNumber(passNumber.trim().toUpperCase())
                .orElseThrow(() -> notFound("Pass was not found"));
        requireMatchingToken(pass, rawToken);
        return pass;
    }

    private void requireMatchingToken(VisitorPass pass, String rawToken) {
        byte[] actual = tokenCrypto.hash(rawToken.trim()).getBytes(StandardCharsets.US_ASCII);
        byte[] expected = pass.getQrTokenHash().getBytes(StandardCharsets.US_ASCII);
        if (!MessageDigest.isEqual(actual, expected)) {
            throw notFound("Pass was not found");
        }
    }

    private PublicPassResponse toPublicResponse(VisitorPass pass) {
        return new PublicPassResponse(pass.getPassNumber(), pass.getVisitorName(), pass.getHostName(),
                pass.getPurpose(), pass.getScheduledAt(), pass.getValidFrom(), pass.getValidUntil(), pass.getStatus());
    }

    private VerifyPassResponse invalid(ScanResult result) {
        return new VerifyPassResponse(false, result, null, null, null, null, null,
                null, null, null, "NONE");
    }

    private ApiException notFound(String message) {
        return new ApiException(HttpStatus.NOT_FOUND, "PASS_NOT_FOUND", message);
    }

    private String fingerprint(String hash) { return hash.substring(0, Math.min(16, hash.length())); }
    private String clean(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
