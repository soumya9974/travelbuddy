package com.travelbuddy.controller;

import com.travelbuddy.model.Itinerary;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import com.travelbuddy.service.ItineraryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItineraryController.class)
@AutoConfigureMockMvc(addFilters = false)
class ItineraryControllerSuccessTest {

    @Autowired MockMvc mockMvc;

    @MockBean ItineraryService itineraryService;
    @MockBean JWTUtil jwtUtil;
    @MockBean UserRepository userRepository;

    @Test
    void createItinerary_success_returnsDto() throws Exception {
        User user = new User();
        user.setId(5L);
        user.setEmail("user@example.com");

        when(jwtUtil.extractUsername("token")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        TravelGroup group = new TravelGroup();
        group.setId(99L);

        Itinerary saved = new Itinerary();
        saved.setId(123L);
        saved.setGroup(group);
        saved.setDay(1);
        saved.setTitle("Day 1");
        saved.setLocation("Center");
        saved.setStartTime(LocalTime.of(9, 0));
        saved.setEndTime(LocalTime.of(10, 0));

        when(itineraryService.createItinerary(eq(99L), any(Itinerary.class), eq(user)))
                .thenReturn(saved);

        mockMvc.perform(
                post("/api/groups/99/itineraries")
                        .header("Authorization", "Bearer token")
                        .contentType("application/json")
                        .content("""
                                {"day":1,"title":"Day 1","location":"Center","startTime":"09:00:00","endTime":"10:00:00"}
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(123))
        .andExpect(jsonPath("$.groupId").value(99))
        .andExpect(jsonPath("$.day").value(1))
        .andExpect(jsonPath("$.title").value("Day 1"));
    }

    @Test
    void updateItinerary_success_resolvesUserAndReturnsDto() throws Exception {
        User user = new User();
        user.setId(8L);
        user.setEmail("user@example.com");

        when(jwtUtil.extractUsername("token")).thenReturn("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        TravelGroup group = new TravelGroup();
        group.setId(77L);

        Itinerary updated = new Itinerary();
        updated.setId(200L);
        updated.setGroup(group);
        updated.setDay(2);
        updated.setTitle("Updated");
        updated.setLocation("New place");

        when(itineraryService.updateItinerary(eq(200L), any(Itinerary.class), eq(user)))
                .thenReturn(updated);

        mockMvc.perform(
                put("/api/itineraries/200")
                        .header("Authorization", "Bearer token")
                        .contentType("application/json")
                        .content("""
                                {"day":2,"title":"Updated","location":"New place"}
                                """)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(200))
        .andExpect(jsonPath("$.groupId").value(77))
        .andExpect(jsonPath("$.day").value(2))
        .andExpect(jsonPath("$.title").value("Updated"));
    }
}
