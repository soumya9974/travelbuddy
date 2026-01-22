package com.travelbuddy.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired JWTUtil jwtUtil;

    @Test
    void nonWhitelistedEndpoint_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/private"))
                .andExpect(status().isForbidden());
    }

    @Test
    void nonWhitelistedEndpoint_withValidJwt_shouldPassSecurity_andReturn404() throws Exception {
        String token = jwtUtil.generateToken("user@example.com", 1L);

        mockMvc.perform(get("/api/private")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
