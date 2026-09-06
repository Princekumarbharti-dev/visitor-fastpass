package com.visitorfastpass.notification.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

class ControllerMethodSecurityTest {
    @Test
    void everyControllerOperationDeclaresMethodSecurity() {
        var unsecured = Arrays.stream(AdminNotificationController.class.getDeclaredMethods())
                .filter(method -> Modifier.isPublic(method.getModifiers()) && !method.isSynthetic())
                .filter(method -> method.getAnnotation(PreAuthorize.class) == null)
                .map(Method::toGenericString).toList();
        assertThat(unsecured).isEmpty();
    }
}
