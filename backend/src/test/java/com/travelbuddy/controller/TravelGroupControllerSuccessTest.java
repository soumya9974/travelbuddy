package com.travelbuddy.controller;

import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import com.travelbuddy.service.TravelGroupService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TravelGroupController.class)
@AutoConfigureMockMvc(addFilters = false)
class TravelGroupControllerSuccessTest {

    @Autowired MockMvc mockMvc;

    @MockBean TravelGroupService travelGroupService;
    @MockBean UserRepository userRepository;
    @MockBean JWTUtil jwtUtil;

    @Test
    void createGroup_success_setsCreatedByAndReturnsDto() throws Exception {
        User creator = new User();
        creator.setId(1L);
        creator.setEmail("creator@example.com");
        creator.setName("Creator");

        when(jwtUtil.extractUsername("token")).thenReturn("creator@example.com");
        when(userRepository.findByEmail("creator@example.com")).thenReturn(Optional.of(creator));

        TravelGroup saved = new TravelGroup();
        saved.setId(10L);
        saved.setName("Paris Trip");
        saved.setDescription("Fun");
        saved.setDestination("Paris");
        saved.setStartDate(LocalDate.of(2026, 2, 1));
        saved.setEndDate(LocalDate.of(2026, 2, 7));
        saved.setCreatedBy(creator);

        when(travelGroupService.createGroup(any(TravelGroup.class))).thenReturn(saved);

        mockMvc.perform(
                post("/api/groups")
                        .header("Authorization", "Bearer token")
                        .contentType("application/json")
                        .content("""
                                {"name":"Paris Trip","description":"Fun","destination":"Paris"}
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.name").value("Paris Trip"))
        .andExpect(jsonPath("$.createdById").value(1))
        .andExpect(jsonPath("$.createdByName").value("Creator"));
    }

    @Test
    void getAllGroups_success_returnsDtoList() throws Exception {
        User creator = new User();
        creator.setId(2L);
        creator.setName("Alice");

        TravelGroup g1 = new TravelGroup();
        g1.setId(100L);
        g1.setName("Rome");
        g1.setCreatedBy(creator);

        TravelGroup g2 = new TravelGroup();
        g2.setId(101L);
        g2.setName("Berlin");
        g2.setCreatedBy(creator);

        when(travelGroupService.getAllGroups()).thenReturn(List.of(g1, g2));

        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].name").value("Rome"))
                .andExpect(jsonPath("$[0].createdById").value(2))
                .andExpect(jsonPath("$[1].id").value(101))
                .andExpect(jsonPath("$[1].name").value("Berlin"));
    }
}
