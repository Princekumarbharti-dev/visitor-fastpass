package com.visitorfastpass.visitor.api;

import com.visitorfastpass.visitor.api.dto.VisitStatusResponse;
import com.visitorfastpass.visitor.api.dto.VisitorRegistrationRequest;
import com.visitorfastpass.visitor.api.dto.VisitorRegistrationResponse;
import com.visitorfastpass.visitor.service.RegistrationService;
import com.visitorfastpass.visitor.service.VisitQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/visits")
@Tag(name = "Public Visits")
public class PublicVisitController {
    private final RegistrationService registrationService;
    private final VisitQueryService queryService;

    public PublicVisitController(RegistrationService registrationService, VisitQueryService queryService) {
        this.registrationService = registrationService;
        this.queryService = queryService;
    }

    @PostMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "Register a new visitor and visit request")
    public ResponseEntity<VisitorRegistrationResponse> register(
            @Valid @RequestBody VisitorRegistrationRequest request) {
        VisitorRegistrationResponse response = registrationService.register(request);
        return ResponseEntity.created(URI.create("/api/v1/public/visits/status/" + response.publicReference()))
                .body(response);
    }

    @GetMapping("/status/{reference}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Look up visitor-safe visit status using the public reference")
    public VisitStatusResponse status(@PathVariable String reference) {
        return queryService.publicStatus(reference);
    }
}
