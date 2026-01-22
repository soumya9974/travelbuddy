package com.travelbuddy.controller;

import com.travelbuddy.dto.ItineraryDTO;

import com.travelbuddy.model.Itinerary;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import com.travelbuddy.service.ItineraryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ItineraryController {

    private final ItineraryService itineraryService;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public ItineraryController(ItineraryService itineraryService, JWTUtil jwtUtil, UserRepository userRepository) {
        this.itineraryService = itineraryService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    // ---------------- GET ALL ITINERARIES (ADMIN / DEBUG) ----------------
    @GetMapping("/itineraries")
    public List<ItineraryDTO> getAllItineraries() {
        return itineraryService.getAllItineraries()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ---------------- GET ITINERARY BY ID ----------------
    @GetMapping("/itineraries/{id}")
    public ItineraryDTO getItineraryById(@PathVariable Long id) {
        return toDTO(itineraryService.getItineraryById(id));
    }

    // ---------------- GET ITINERARIES BY GROUP ----------------
    @GetMapping("/groups/{groupId}/itineraries")
    public List<ItineraryDTO> getGroupItineraries(@PathVariable Long groupId) {
        return itineraryService.getItinerariesByGroup(groupId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ---------------- CREATE ITINERARY ----------------
    @PostMapping("/groups/{groupId}/itineraries")
    public ItineraryDTO createItinerary(
            @PathVariable Long groupId,
            @RequestBody Itinerary itinerary,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return toDTO(itineraryService.createItinerary(groupId, itinerary, user));
    }
    
    @PutMapping("/itineraries/{id}")
    public ItineraryDTO updateItinerary(
            @PathVariable Long id,
            @RequestBody Itinerary updated,
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = resolveUser(authHeader);
        return toDTO(itineraryService.updateItinerary(id, updated, user));
    }



    // ---------------- DELETE ITINERARY ----------------
    @DeleteMapping("/itineraries/{id}")
    public void deleteItinerary(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = resolveUser(authHeader);
        itineraryService.deleteItinerary(id, user);
    }
    
 // ---------------- AUTH HELPER ----------------
    private User resolveUser(String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }



    // ---------------- DTO MAPPER ----------------
    private ItineraryDTO toDTO(Itinerary itinerary) {

        ItineraryDTO dto = new ItineraryDTO();
        dto.setId(itinerary.getId());
        dto.setDay(itinerary.getDay());
        dto.setTitle(itinerary.getTitle());
        dto.setDescription(itinerary.getDescription());
        dto.setLocation(itinerary.getLocation());

        // ✅ SAFE group handling
        if (itinerary.getGroup() != null) {
            dto.setGroupId(itinerary.getGroup().getId());
        }

        if (itinerary.getStartTime() != null) {
            dto.setStartTime(itinerary.getStartTime().toString());
        }

        if (itinerary.getEndTime() != null) {
            dto.setEndTime(itinerary.getEndTime().toString());
        }

        return dto;
    }
}
