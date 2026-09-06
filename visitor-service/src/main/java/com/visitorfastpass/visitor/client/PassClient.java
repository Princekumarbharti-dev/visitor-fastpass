package com.visitorfastpass.visitor.client;

import java.time.OffsetDateTime;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pass-service", path = "/api/v1/internal/passes")
public interface PassClient {
    @PostMapping
    GeneratePassResponse generate(
            @RequestHeader("X-Internal-Api-Key") String internalApiKey,
            @RequestBody GeneratePassRequest request);

    @PostMapping("/verify")
    VerifyPassResponse verify(@RequestHeader("X-Internal-Api-Key") String internalApiKey,
                              @RequestBody VerifyPassRequest request);

    @PatchMapping("/{visitId}/use")
    void markUsed(@RequestHeader("X-Internal-Api-Key") String internalApiKey,
                  @PathVariable("visitId") long visitId,
                  @RequestBody UsePassRequest request);

    record GeneratePassRequest(
            Long visitId,
            String publicReference,
            String visitorName,
            String visitorEmail,
            String hostName,
            String purpose,
            OffsetDateTime scheduledAt
    ) {}

    record GeneratePassResponse(String passNumber, String status) {}
    record VerifyPassRequest(String token, String location) {}
    record UsePassRequest(String token) {}
    record VerifyPassResponse(boolean valid, String result, Long visitId, String passNumber, String allowedAction) {}
}
