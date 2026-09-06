package com.visitorfastpass.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.security.user.name=test-config",
        "spring.security.user.password=test-password"
})
@AutoConfigureMockMvc
class ConfigServerSecurityTest {
    @Autowired MockMvc mockMvc;

    @Test
    void configurationRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/identity-service/default"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void configurationAcceptsValidBasicCredentials() throws Exception {
        mockMvc.perform(get("/identity-service/default")
                        .with(httpBasic("test-config", "test-password")))
                .andExpect(status().isOk());
    }

    @Test
    void healthIsPublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }
}
