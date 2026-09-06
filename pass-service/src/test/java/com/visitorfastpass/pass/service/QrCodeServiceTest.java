package com.visitorfastpass.pass.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class QrCodeServiceTest {
    @Test
    void rendersPngQrCode() {
        byte[] png = new QrCodeService().png("https://example.test/pass/FP-1?token=secure");
        assertThat(png).startsWith((byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47);
        assertThat(png.length).isGreaterThan(300);
    }
}
