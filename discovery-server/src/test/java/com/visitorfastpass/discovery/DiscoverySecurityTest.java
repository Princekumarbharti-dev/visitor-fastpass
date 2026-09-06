package com.visitorfastpass.discovery;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.security.user.name=test-eureka",
        "spring.security.user.password=test-password"
})
@AutoConfigureMockMvc
class DiscoverySecurityTest {
    @Autowired MockMvc mockMvc;

    @Test
    void dashboardRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void dashboardAcceptsValidBasicCredentials() throws Exception {
        mockMvc.perform(get("/").with(httpBasic("test-eureka", "test-password")))
                .andExpect(status().isOk());
    }

    @Test
    void healthIsPublicForContainerOrchestration() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}
