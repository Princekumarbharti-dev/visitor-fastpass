package com.visitorfastpass.identity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap")
public record BootstrapProperties(
        boolean enabled,
        Account admin,
        Account host,
        Account reception
) {
    public record Account(
            String username,
            String password,
            String email,
            String employeeCode,
            String fullName,
            String department,
            String designation
    ) {}
}
