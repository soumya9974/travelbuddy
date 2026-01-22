package com.travelbuddy.service;

import com.travelbuddy.model.Membership;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.MembershipRepository;
import com.travelbuddy.websocket.WebSocketSessionRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MembershipServiceTest {

    @Mock MembershipRepository membershipRepository;
    @Mock SimpMessagingTemplate messagingTemplate;
    @Mock WebSocketSessionRegistry sessionRegistry;

    @InjectMocks MembershipService membershipService;

    @Test
    void joinGroup_alreadyMember_shouldThrowException() {
        when(membershipRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.of(new Membership()));

        User user = new User();
        user.setId(1L);

        TravelGroup group = new TravelGroup();
        group.setId(1L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> membershipService.joinGroup(user, group));

        assertEquals("User already joined this group", ex.getMessage());
        verify(membershipRepository, never()).save(any());
    }

    @Test
    void joinGroup_notMember_shouldSave() {
        when(membershipRepository.findByUserIdAndGroupId(1L, 1L)).thenReturn(Optional.empty());
        when(membershipRepository.save(any(Membership.class))).thenAnswer(i -> i.getArgument(0));

        User user = new User();
        user.setId(1L);

        TravelGroup group = new TravelGroup();
        group.setId(1L);

        Membership saved = membershipService.joinGroup(user, group);

        assertEquals(user, saved.getUser());
        assertEquals(group, saved.getGroup());
        assertEquals("MEMBER", saved.getRole());
        assertNotNull(saved.getJoinedAt());
    }

    @Test
    void leaveGroup_shouldDeleteAndForceDisconnect_whenSessionsExist() {
        when(sessionRegistry.getSessions(1L)).thenReturn(Set.of("s1", "s2"));

        membershipService.leaveGroup(9L, 1L);

        verify(membershipRepository).deleteByUserIdAndGroupId(1L, 9L);
        verify(messagingTemplate, times(2)).convertAndSendToUser(eq("1"), eq("/queue/disconnect"), eq("FORCE_DISCONNECT"));
    }
}
