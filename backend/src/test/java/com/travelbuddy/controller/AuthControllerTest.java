package com.travelbuddy.controller;

import com.travelbuddy.model.User;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JWTUtil jwtUtil;

    @Test
    void login_invalidCredentials_shouldReturn500() throws Exception {
        User u = new User();
        u.setEmail("test@test.com");
        u.setPasswordHash("hashed");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"email":"test@test.com","password":"wrong"}
                                """))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Invalid credentials"))
                .andExpect(jsonPath("$.status").value(500));
    }

    @Test
    void login_validCredentials_shouldReturn200_withToken() throws Exception {
        User u = new User();
        u.setId(1L);
        u.setEmail("test@test.com");
        u.setPasswordHash("hashed");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("correct", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken("test@test.com", 1L)).thenReturn("token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("""
                                {"email":"test@test.com","password":"correct"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.user.email").value("test@test.com"));
    }
}
