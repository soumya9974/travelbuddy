package com.travelbuddy.controller;

import com.travelbuddy.dto.MembershipDTO;


import com.travelbuddy.model.Membership;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.TravelGroupRepository;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import com.travelbuddy.service.MembershipService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/memberships")
public class MembershipController {

    private final MembershipService membershipService;
    private final UserRepository userRepository;
    private final TravelGroupRepository groupRepository;
    private final JWTUtil jwtUtil;

    public MembershipController(
            MembershipService membershipService,
            UserRepository userRepository,
            TravelGroupRepository groupRepository,
            JWTUtil jwtUtil
    ) {
        this.membershipService = membershipService;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.jwtUtil = jwtUtil;
    }

    // -------- GET MEMBERS OF GROUP --------
    @GetMapping
    public List<MembershipDTO> getMembers(@PathVariable Long groupId) {
        return membershipService.getMembershipsByGroupId(groupId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // -------- JOIN GROUP --------
    @PostMapping
    public MembershipDTO joinGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TravelGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        return toDTO(membershipService.joinGroup(user, group));
    }
    
    // -----------LEAVE GROUP ------------
    @DeleteMapping
    public void leaveGroup(
            @PathVariable Long groupId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        membershipService.leaveGroup(groupId, user.getId());
    }


    // -------- DTO MAPPER --------
    private MembershipDTO toDTO(Membership membership) {
        MembershipDTO dto = new MembershipDTO();
        dto.setId(membership.getId());
        dto.setRole(membership.getRole());
        dto.setJoinedAt(membership.getJoinedAt());

        dto.setUserId(membership.getUser().getId());
        dto.setUserName(membership.getUser().getUsername());

        dto.setGroupId(membership.getGroup().getId());
        dto.setGroupName(membership.getGroup().getName());

        return dto;
    }
}
