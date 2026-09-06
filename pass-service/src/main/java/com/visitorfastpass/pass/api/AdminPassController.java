package com.visitorfastpass.pass.api;

import com.visitorfastpass.pass.api.dto.PublicPassResponse;
import com.visitorfastpass.pass.api.dto.RevokePassRequest;
import com.visitorfastpass.pass.service.VisitorPassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/passes")
@Tag(name = "Admin Passes")
@SecurityRequirement(name = "bearerAuth")
public class AdminPassController {
    private final VisitorPassService passService;

    public AdminPassController(VisitorPassService passService) { this.passService = passService; }

    @PatchMapping("/{passId}/revoke")
    @PreAuthorize("hasAuthority('PASS_REVOKE')")
    @Operation(summary = "Revoke an active or used visitor pass")
    public PublicPassResponse revoke(@PathVariable long passId,
                                     @Valid @RequestBody RevokePassRequest request) {
        return passService.revoke(passId, request.reason());
    }
}
