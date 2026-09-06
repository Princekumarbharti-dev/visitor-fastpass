package com.visitorfastpass.notification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mail")
public record MailDeliveryProperties(boolean enabled, String fromAddress) {}
