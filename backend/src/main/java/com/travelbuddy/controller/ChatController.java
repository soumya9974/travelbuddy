package com.travelbuddy.controller;

import com.travelbuddy.dto.ChatMessageDTO;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.service.MessageService;
import com.travelbuddy.service.MembershipService;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final MembershipService membershipService;
    private final UserRepository userRepository;

    public ChatController(
            SimpMessagingTemplate messagingTemplate,
            MessageService messageService,
            MembershipService membershipService,
            UserRepository userRepository
    ) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.membershipService = membershipService;
        this.userRepository = userRepository;
    }

    @MessageMapping("/groups/{groupId}/chat")
    public void sendMessage(
            @DestinationVariable Long groupId,
            @Payload ChatMessageDTO message,
            Principal principal
    ) {
        Long userId = (Long) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!membershipService.isMember(groupId, userId)) {
            throw new AccessDeniedException("You are not a member of this group");
        }

        String type = message.getType() == null ? "CHAT" : message.getType();
        String content = message.getContent() == null ? "" : message.getContent().trim();

        // ✅ 1) TYPING: broadcast only, never persist
        if ("TYPING".equalsIgnoreCase(type)) {
            ChatMessageDTO typing = new ChatMessageDTO();
            typing.setGroupId(groupId);
            typing.setSenderId(user.getId());
            typing.setSenderName(user.getUsername());
            typing.setContent(""); // don't care
            typing.setTimestamp(LocalDateTime.now());
            typing.setType("TYPING");

            messagingTemplate.convertAndSend("/topic/groups/" + groupId, typing);
            return;
        }

        // ✅ 2) CHAT: ignore blank messages completely
        if (content.isEmpty()) {
            return;
        }

        // ✅ 3) Save + broadcast real chat message
        messageService.saveMessage(groupId, user, content);

        ChatMessageDTO outbound = new ChatMessageDTO();
        outbound.setGroupId(groupId);
        outbound.setSenderId(user.getId());
        outbound.setSenderName(user.getUsername());
        outbound.setContent(content);
        outbound.setTimestamp(LocalDateTime.now());
        outbound.setType("CHAT");

        messagingTemplate.convertAndSend("/topic/groups/" + groupId, outbound);
    }

    @MessageMapping("/groups/{groupId}/presence")
    public void handlePresence(
            @DestinationVariable Long groupId,
            @Payload ChatMessageDTO message,
            Principal principal
    ) {
        Long userId = (Long) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        if (!membershipService.isMember(groupId, userId)) {
            throw new AccessDeniedException("Not a group member");
        }

        messagingTemplate.convertAndSend(
                "/topic/groups/" + groupId + "/presence",
                message
        );
    }
}
