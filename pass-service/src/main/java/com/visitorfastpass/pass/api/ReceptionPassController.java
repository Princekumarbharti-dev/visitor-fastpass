package com.visitorfastpass.pass.api;

import com.visitorfastpass.pass.api.dto.VerifyPassRequest;
import com.visitorfastpass.pass.api.dto.VerifyPassResponse;
import com.visitorfastpass.pass.security.CurrentUser;
import com.visitorfastpass.pass.service.VisitorPassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reception/passes")
@Tag(name = "Reception Pass Verification")
@SecurityRequirement(name = "bearerAuth")
public class ReceptionPassController {
    private final VisitorPassService passService;
    private final CurrentUser currentUser;

    public ReceptionPassController(VisitorPassService passService, CurrentUser currentUser) {
        this.passService = passService;
        this.currentUser = currentUser;
    }

    @PostMapping("/verify")
    @PreAuthorize("hasAuthority('PASS_VERIFY')")
    @Operation(summary = "Verify a scanned QR token and record the attempt")
    public VerifyPassResponse verify(@Valid @RequestBody VerifyPassRequest request) {
        return passService.verify(request.token(), request.location(), currentUser.userIdOrNull());
    }
}
