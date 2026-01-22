package com.travelbuddy.service;

import com.travelbuddy.model.Membership;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.MembershipRepository;
import com.travelbuddy.websocket.WebSocketSessionRegistry;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionRegistry sessionRegistry;

    public MembershipService(MembershipRepository membershipRepository, SimpMessagingTemplate messagingTemplate, WebSocketSessionRegistry sessionRegistry) {
        this.membershipRepository = membershipRepository;
        this.messagingTemplate = messagingTemplate;
        this.sessionRegistry = sessionRegistry;
    }

    // ---------------- JOIN GROUP ----------------
    public Membership joinGroup(User user, TravelGroup group) {

        // Prevent duplicate membership
        membershipRepository.findByUserIdAndGroupId(user.getId(), group.getId())
                .ifPresent(m -> {
                    throw new RuntimeException("User already joined this group");
                });

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setGroup(group);
        membership.setRole("MEMBER");
        membership.setJoinedAt(LocalDateTime.now());

        return membershipRepository.save(membership);
    }

    // ---------------- GET MEMBERS BY GROUP ----------------
    public List<Membership> getMembershipsByGroupId(Long groupId) {
        return membershipRepository.findByGroupId(groupId);
    }

    // ---------------- LEAVE GROUP ----------------
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {

        membershipRepository.deleteByUserIdAndGroupId(userId, groupId);

        // Force WS disconnect
        Set<String> sessions = sessionRegistry.getSessions(userId);
        if (sessions != null) {
            sessions.forEach(sessionId ->
                    messagingTemplate.convertAndSendToUser(
                            userId.toString(),
                            "/queue/disconnect",
                            "FORCE_DISCONNECT"
                    )
            );
        }
    }


    public boolean isMember(Long groupId, Long userId) {
        boolean result = membershipRepository.existsByGroupIdAndUserId(groupId, userId);
        return result;
    }

}
