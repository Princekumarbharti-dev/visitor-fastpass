package com.visitorfastpass.pass.api;

import com.visitorfastpass.pass.api.dto.GeneratePassRequest;
import com.visitorfastpass.pass.api.dto.GeneratePassResponse;
import com.visitorfastpass.pass.api.dto.PublicPassResponse;
import com.visitorfastpass.pass.api.dto.UsePassRequest;
import com.visitorfastpass.pass.api.dto.VerifyPassRequest;
import com.visitorfastpass.pass.api.dto.VerifyPassResponse;
import com.visitorfastpass.pass.service.VisitorPassService;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/internal/passes")
@Hidden
public class InternalPassController {
    private final VisitorPassService passService;

    public InternalPassController(VisitorPassService passService) { this.passService = passService; }

    @PostMapping
    @PreAuthorize("hasAuthority('INTERNAL_SERVICE')")
    public GeneratePassResponse generate(@Valid @RequestBody GeneratePassRequest request) {
        return passService.generate(request);
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAuthority('INTERNAL_SERVICE')")
    public VerifyPassResponse verify(@Valid @RequestBody VerifyPassRequest request) {
        return passService.verify(request.token(), request.location(), null);
    }

    @PatchMapping("/{visitId}/use")
    @PreAuthorize("hasAuthority('INTERNAL_SERVICE')")
    public PublicPassResponse markUsed(@PathVariable long visitId, @Valid @RequestBody UsePassRequest request) {
        return passService.markUsed(visitId, request.token(), null);
    }
}
