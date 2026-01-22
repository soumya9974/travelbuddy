package com.travelbuddy.controller;

import com.travelbuddy.dto.ChatMessageDTO;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.service.MessageService;
import com.travelbuddy.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @Mock SimpMessagingTemplate messagingTemplate;
    @Mock MessageService messageService;
    @Mock MembershipService membershipService;
    @Mock UserRepository userRepository;

    @InjectMocks ChatController chatController;

    private Principal principalWithUserId(long userId) {
        return new UsernamePasswordAuthenticationToken(userId, null);
    }

    @Test
    void sendMessage_typing_shouldBroadcastOnly() {
        long groupId = 9L;
        long userId = 1L;

        User u = new User();
        u.setId(userId);
        u.setUsername("alice");

        when(userRepository.findById(userId)).thenReturn(Optional.of(u));
        when(membershipService.isMember(groupId, userId)).thenReturn(true);

        ChatMessageDTO msg = new ChatMessageDTO();
        msg.setType("TYPING");

        chatController.sendMessage(groupId, msg, principalWithUserId(userId));

        verify(messagingTemplate).convertAndSend(eq("/topic/groups/" + groupId), any(ChatMessageDTO.class));
        verify(messageService, never()).saveMessage(anyLong(), any(User.class), anyString());
    }

    @Test
    void sendMessage_blankChat_shouldDoNothing() {
        long groupId = 9L;
        long userId = 1L;

        User u = new User();
        u.setId(userId);
        u.setUsername("alice");

        when(userRepository.findById(userId)).thenReturn(Optional.of(u));
        when(membershipService.isMember(groupId, userId)).thenReturn(true);

        ChatMessageDTO msg = new ChatMessageDTO();
        msg.setType("CHAT");
        msg.setContent("   ");

        chatController.sendMessage(groupId, msg, principalWithUserId(userId));

        verify(messageService, never()).saveMessage(anyLong(), any(User.class), anyString());
        verify(messagingTemplate, never()).convertAndSend(anyString(), any(Object.class));
    }

    @Test
    void sendMessage_chat_shouldSaveAndBroadcast() {
        long groupId = 9L;
        long userId = 1L;

        User u = new User();
        u.setId(userId);
        u.setUsername("alice");

        when(userRepository.findById(userId)).thenReturn(Optional.of(u));
        when(membershipService.isMember(groupId, userId)).thenReturn(true);

        ChatMessageDTO msg = new ChatMessageDTO();
        msg.setContent("hi");

        chatController.sendMessage(groupId, msg, principalWithUserId(userId));

        verify(messageService).saveMessage(groupId, u, "hi");
        verify(messagingTemplate).convertAndSend(eq("/topic/groups/" + groupId), any(ChatMessageDTO.class));
    }

    @Test
    void sendMessage_notMember_shouldThrowAccessDenied() {
        long groupId = 9L;
        long userId = 1L;

        User u = new User();
        u.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(u));
        when(membershipService.isMember(groupId, userId)).thenReturn(false);

        ChatMessageDTO msg = new ChatMessageDTO();
        msg.setContent("hi");

        assertThrows(AccessDeniedException.class,
                () -> chatController.sendMessage(groupId, msg, principalWithUserId(userId)));
    }

    @Test
    void handlePresence_member_shouldBroadcast() {
        long groupId = 9L;
        long userId = 1L;

        when(membershipService.isMember(groupId, userId)).thenReturn(true);

        ChatMessageDTO presence = new ChatMessageDTO();
        presence.setType("JOIN");

        chatController.handlePresence(groupId, presence, principalWithUserId(userId));

        verify(messagingTemplate).convertAndSend(eq("/topic/groups/" + groupId + "/presence"), eq(presence));
    }
}
