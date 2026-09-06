package com.visitorfastpass.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.config.import=",
        "eureka.client.enabled=false",
        "app.jwt.secret=test-secret-that-is-at-least-thirty-two-bytes-long"
})
class GatewaySecurityTest {
    @LocalServerPort int port;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void protectedAdminRouteRejectsAnonymousRequestWithStructuredJson() {
        webTestClient.get()
                .uri("/api/v1/admin/employees")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectHeader().contentTypeCompatibleWith("application/json")
                .expectBody()
                .jsonPath("$.code").value(code -> assertThat(code).isIn(
                        "AUTHENTICATION_REQUIRED", "INVALID_TOKEN"));
    }

    @Test
    void healthEndpointIsPublic() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk();
    }
}
