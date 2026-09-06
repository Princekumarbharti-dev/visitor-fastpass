package com.visitorfastpass.pass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.pass")
public record PassProperties(
        String encryptionSecret,
        String publicBaseUrl,
        long validBeforeMinutes,
        long validAfterMinutes
) {
}
