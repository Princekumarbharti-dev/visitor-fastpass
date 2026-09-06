package com.visitorfastpass.visitor.api;

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
        List<Class<?>> controllers = List.of(
                PublicVisitController.class,
                HostVisitController.class,
                AdminVisitController.class,
                ReceptionVisitController.class);

        List<String> unsecured = controllers.stream()
                .flatMap(type -> Arrays.stream(type.getDeclaredMethods()))
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> !method.isSynthetic())
                .filter(method -> method.getAnnotation(PreAuthorize.class) == null)
                .map(Method::toGenericString)
                .toList();

        assertThat(unsecured)
                .as("Every public controller operation must declare @PreAuthorize")
                .isEmpty();
    }
}
