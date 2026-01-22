package com.travelbuddy.controller;

import com.travelbuddy.model.User;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import com.travelbuddy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerSuccessTest {

  @Autowired MockMvc mockMvc;

  @MockBean UserService userService;
  @MockBean UserRepository userRepository;
  @MockBean JWTUtil jwtUtil;
  @MockBean PasswordEncoder passwordEncoder;


    @Test
    void getAllUsers_shouldReturn200_andList() throws Exception {
        User u1 = new User();
        u1.setId(1L);
        u1.setName("A");
        u1.setEmail("a@test.com");
        User u2 = new User();
        u2.setId(2L);
        u2.setName("B");
        u2.setEmail("b@test.com");

        when(userService.getAllUsers()).thenReturn(List.of(u1, u2));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].email").value("b@test.com"));
    }

    @Test
    void createUser_shouldReturn200_andUserDto() throws Exception {
        User saved = new User();
        saved.setId(10L);
        saved.setName("New");
        saved.setEmail("new@test.com");

        when(userService.createUser(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New\",\"email\":\"new@test.com\",\"username\":\"new\",\"passwordHash\":\"x\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.email").value("new@test.com"));
    }

    @Test
    void resetPassword_shouldReturn200() throws Exception {
        doNothing().when(userService).resetPassword("a@test.com", "newpass");

        mockMvc.perform(post("/api/users/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@test.com\",\"password\":\"newpass\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successful"));
    }

    @Test
    void deleteUser_shouldReturn200() throws Exception {
        doNothing().when(userService).deleteUser(5L);

        mockMvc.perform(delete("/api/users/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }
}
