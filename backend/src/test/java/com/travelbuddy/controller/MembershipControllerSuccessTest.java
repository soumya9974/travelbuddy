package com.travelbuddy.controller;

import com.travelbuddy.model.Membership;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.TravelGroupRepository;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import com.travelbuddy.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MembershipController.class)
@AutoConfigureMockMvc(addFilters = false)
class MembershipControllerSuccessTest {

    @Autowired MockMvc mockMvc;

    @MockBean MembershipService membershipService;
    @MockBean UserRepository userRepository;
    @MockBean TravelGroupRepository travelGroupRepository;
    @MockBean JWTUtil jwtUtil;

    @Test
    void getMembers_shouldReturn200() throws Exception {
        User u = new User();
        u.setId(1L);
        u.setUsername("alice");

        TravelGroup g = new TravelGroup();
        g.setId(9L);
        g.setName("Trip");

        Membership m = new Membership();
        m.setId(100L);
        m.setUser(u);
        m.setGroup(g);
        m.setRole("MEMBER");
        m.setJoinedAt(LocalDateTime.now());

        when(membershipService.getMembershipsByGroupId(9L)).thenReturn(List.of(m));

        mockMvc.perform(get("/api/groups/9/memberships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].userName").value("alice"))
                .andExpect(jsonPath("$[0].groupId").value(9));
    }

    @Test
    void joinGroup_shouldReturn200() throws Exception {
        when(jwtUtil.extractUsername("tok")).thenReturn("a@test.com");

        User u = new User();
        u.setId(1L);
        u.setUsername("alice");
        u.setEmail("a@test.com");

        TravelGroup g = new TravelGroup();
        g.setId(9L);
        g.setName("Trip");

        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(u));
        when(travelGroupRepository.findById(9L)).thenReturn(Optional.of(g));

        Membership saved = new Membership();
        saved.setId(200L);
        saved.setUser(u);
        saved.setGroup(g);
        saved.setRole("MEMBER");
        saved.setJoinedAt(LocalDateTime.now());

        when(membershipService.joinGroup(u, g)).thenReturn(saved);

        mockMvc.perform(post("/api/groups/9/memberships")
                        .header("Authorization", "Bearer tok")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(200))
                .andExpect(jsonPath("$.groupName").value("Trip"));
    }

    @Test
    void leaveGroup_shouldReturn200() throws Exception {
        when(jwtUtil.extractUsername("tok")).thenReturn("a@test.com");

        User u = new User();
        u.setId(1L);
        u.setEmail("a@test.com");

        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(u));
        doNothing().when(membershipService).leaveGroup(9L, 1L);

        mockMvc.perform(delete("/api/groups/9/memberships")
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk());

        verify(membershipService).leaveGroup(9L, 1L);
    }
}
