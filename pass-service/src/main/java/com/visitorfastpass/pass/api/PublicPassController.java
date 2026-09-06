package com.visitorfastpass.pass.api;

import com.visitorfastpass.pass.api.dto.PublicPassResponse;
import com.visitorfastpass.pass.service.QrCodeService;
import com.visitorfastpass.pass.service.VisitorPassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/passes")
@Tag(name = "Public Pass")
public class PublicPassController {
    private final VisitorPassService passService;
    private final QrCodeService qrCodeService;

    public PublicPassController(VisitorPassService passService, QrCodeService qrCodeService) {
        this.passService = passService;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/{passNumber}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Display a digital pass using its secret token")
    public PublicPassResponse display(@PathVariable String passNumber, @RequestParam String token) {
        return passService.publicPass(passNumber, token);
    }

    @GetMapping(value = "/{passNumber}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("permitAll()")
    @Operation(summary = "Render the pass verification URL as a QR PNG")
    public ResponseEntity<byte[]> qr(@PathVariable String passNumber, @RequestParam String token) {
        byte[] png = qrCodeService.png(passService.qrValue(passNumber, token));
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).contentType(MediaType.IMAGE_PNG).body(png);
    }
}
