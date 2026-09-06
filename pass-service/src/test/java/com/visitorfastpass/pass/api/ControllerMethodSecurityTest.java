package com.visitorfastpass.pass.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.prepost.PreAuthorize;

class ControllerMethodSecurityTest {
    @Test
    void everyPublicControllerOperationDeclaresMethodSecurity() {
        List<Class<?>> controllers = List.of(InternalPassController.class, PublicPassController.class,
                ReceptionPassController.class, AdminPassController.class);
        List<String> unsecured = controllers.stream()
                .flatMap(type -> Arrays.stream(type.getDeclaredMethods()))
                .filter(method -> Modifier.isPublic(method.getModifiers()) && !method.isSynthetic())
                .filter(method -> method.getAnnotation(PreAuthorize.class) == null)
                .map(Method::toGenericString).toList();
        assertThat(unsecured).as("Every controller operation requires @PreAuthorize").isEmpty();
    }
}
