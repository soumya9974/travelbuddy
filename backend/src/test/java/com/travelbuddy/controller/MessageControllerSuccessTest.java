package com.travelbuddy.controller;

import com.travelbuddy.model.Membership;
import com.travelbuddy.model.Message;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.MembershipRepository;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import com.travelbuddy.service.MessageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MessageController.class)
@AutoConfigureMockMvc(addFilters = false)
class MessageControllerSuccessTest {

    @Autowired MockMvc mockMvc;

    @MockBean MessageService messageService;
    @MockBean MembershipRepository membershipRepository;
    @MockBean SimpMessagingTemplate messagingTemplate;
    @MockBean JWTUtil jwtUtil;
    @MockBean UserRepository userRepository;

    @Test
    void getMessages_shouldReturn200_andDtos() throws Exception {
        User sender = new User();
        sender.setId(1L);
        sender.setUsername("alice");

        TravelGroup g = new TravelGroup();
        g.setId(9L);

        Message m = new Message();
        m.setId(100L);
        m.setSender(sender);
        m.setGroup(g);
        m.setContent("hi");
        m.setSentAt(LocalDateTime.now());

        when(messageService.getMessagesByGroup(9L)).thenReturn(List.of(m));

        mockMvc.perform(get("/api/groups/9/messages"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].senderName").value("alice"))
                .andExpect(jsonPath("$[0].type").value("CHAT"));
    }

    @Test
    void deleteMessage_asAdmin_shouldReturn200_andBroadcastDelete() throws Exception {
        when(jwtUtil.extractUsername("tok")).thenReturn("a@test.com");

        User admin = new User();
        admin.setId(1L);
        admin.setEmail("a@test.com");

        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(admin));

        Membership membership = new Membership();
        membership.setRole("ADMIN");
        when(membershipRepository.findByUserIdAndGroupId(1L, 9L)).thenReturn(Optional.of(membership));

        TravelGroup g = new TravelGroup();
        g.setId(9L);

        Message msg = new Message();
        msg.setId(100L);
        msg.setGroup(g);

        when(messageService.getMessage(100L)).thenReturn(msg);
        doNothing().when(messageService).deleteMessageById(100L);

        mockMvc.perform(delete("/api/groups/9/messages/100")
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk());

        verify(messageService).deleteMessageById(100L);
        verify(messagingTemplate).convertAndSend(eq("/topic/groups/9"), any(Object.class));
    }

    @Test
    void deleteAllMessages_asAdmin_shouldReturn200_andBroadcastDeleteAll() throws Exception {
        when(jwtUtil.extractUsername("tok")).thenReturn("a@test.com");

        User admin = new User();
        admin.setId(1L);
        admin.setEmail("a@test.com");

        when(userRepository.findByEmail("a@test.com")).thenReturn(Optional.of(admin));

        Membership membership = new Membership();
        membership.setRole("ADMIN");
        when(membershipRepository.findByUserIdAndGroupId(1L, 9L)).thenReturn(Optional.of(membership));

        doNothing().when(messageService).deleteAllMessagesByGroupId(9L);

        mockMvc.perform(delete("/api/groups/9/messages")
                        .header("Authorization", "Bearer tok"))
                .andExpect(status().isOk());

        verify(messageService).deleteAllMessagesByGroupId(9L);
        verify(messagingTemplate).convertAndSend(eq("/topic/groups/9"), any(Object.class));
    }
}
