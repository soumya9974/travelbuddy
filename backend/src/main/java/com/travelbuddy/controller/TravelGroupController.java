package com.travelbuddy.controller;

import com.travelbuddy.dto.TravelGroupDTO;

import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import com.travelbuddy.service.TravelGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class TravelGroupController {

    private final TravelGroupService travelGroupService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public TravelGroupController(
            TravelGroupService travelGroupService,
            UserRepository userRepository,
            JWTUtil jwtUtil
    ) {
        this.travelGroupService = travelGroupService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    // ---------------- GET ALL GROUPS ----------------
    @GetMapping
    public List<TravelGroupDTO> getAllGroups() {
        return travelGroupService.getAllGroups()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ---------------- GET GROUP BY ID ----------------
    @GetMapping("/{id}")
    public TravelGroupDTO getGroupById(@PathVariable Long id) {
        return toDTO(travelGroupService.getGroupById(id));
    }

    // ---------------- CREATE GROUP ----------------
    @PostMapping
    public TravelGroupDTO createGroup(
            @RequestBody TravelGroup group,
            @RequestHeader("Authorization") String authHeader
    ) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 🔥 CRITICAL FIX
        group.setCreatedBy(user);

        return toDTO(travelGroupService.createGroup(group));
    }

    // ---------------- UPDATE GROUP ----------------
    @PutMapping("/{id}")
    public TravelGroupDTO updateGroup(
            @PathVariable Long id,
            @RequestBody TravelGroup updatedGroup
    ) {
        return toDTO(travelGroupService.updateGroup(id, updatedGroup));
    }

 // ---------------- DELETE GROUP (ADMIN ONLY) ----------------
    @DeleteMapping("/{groupId}")
    public void deleteGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        travelGroupService.deleteGroup(groupId, user);
    }

    // ---------------- DTO MAPPER ----------------
    private TravelGroupDTO toDTO(TravelGroup group) {

        TravelGroupDTO dto = new TravelGroupDTO();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setDescription(group.getDescription());
        dto.setDestination(group.getDestination());
        dto.setStartDate(group.getStartDate());
        dto.setEndDate(group.getEndDate());

        if (group.getCreatedBy() != null) {
            dto.setCreatedById(group.getCreatedBy().getId());
            dto.setCreatedByName(group.getCreatedBy().getName());
        }

        return dto;
    }
}
