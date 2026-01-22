package com.travelbuddy.controller;

import com.travelbuddy.dto.ChatMessageDTO;
import com.travelbuddy.model.Membership;
import com.travelbuddy.model.Message;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.MembershipRepository;
import com.travelbuddy.repository.UserRepository;
import com.travelbuddy.security.JWTUtil;
import com.travelbuddy.service.MessageService;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/messages")
public class MessageController {

	private final MessageService messageService;
	private final MembershipRepository membershipRepository;
	private final SimpMessagingTemplate messagingTemplate;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

	public MessageController(MessageService messageService, MembershipRepository membershipRepository, SimpMessagingTemplate messagingTemplate, JWTUtil jwtUtil, UserRepository userRepository) {
		this.messageService = messageService;
		this.membershipRepository =  membershipRepository;
		this.messagingTemplate = messagingTemplate;
		this.jwtUtil = jwtUtil;
		this.userRepository = userRepository;
	}

	@GetMapping
	public List<ChatMessageDTO> getMessages(@PathVariable Long groupId) {
		return messageService.getMessagesByGroup(groupId).stream().map(m -> {
			ChatMessageDTO dto = new ChatMessageDTO();
			dto.setId(m.getId());
			dto.setGroupId(groupId);
			dto.setSenderId(m.getSender().getId());
			dto.setSenderName(m.getSender().getUsername()); // ✅
			dto.setContent(m.getContent());
			dto.setTimestamp(m.getSentAt());
			dto.setType("CHAT");
			return dto;
		}).toList();
	}

	@DeleteMapping("/{messageId}")
    public void deleteMessage(
            @PathVariable Long groupId,
            @PathVariable Long messageId,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Membership membership = membershipRepository
                .findByUserIdAndGroupId(user.getId(), groupId)
                .orElseThrow(() -> new RuntimeException("Not a group member"));

        if (!"ADMIN".equals(membership.getRole())) {
            throw new RuntimeException("Only admins can delete messages");
        }

        Message msg = messageService.getMessage(messageId);

        // ✅ Prevent deleting messages from other groups
        if (!msg.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("Message does not belong to this group");
        }

        messageService.deleteMessageById(messageId);
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setType("DELETE");
        dto.setId(messageId);
        messagingTemplate.convertAndSend("/topic/groups/" + groupId, dto);
    }
    
    @DeleteMapping
    public void deleteAllMessagesInGroup(@PathVariable Long groupId,
                                         @RequestHeader("Authorization") String authHeader) {
    	String token = authHeader.replace("Bearer ", "");
    	String email = jwtUtil.extractUsername(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Membership membership = membershipRepository
                .findByUserIdAndGroupId(user.getId(), groupId)
                .orElseThrow(() -> new RuntimeException("Not a group member"));

        if (!"ADMIN".equals(membership.getRole())) {
            throw new RuntimeException("Only admins can delete messages");
        }

       // same ADMIN check
       messageService.deleteAllMessagesByGroupId(groupId);
       
       ChatMessageDTO dto = new ChatMessageDTO();
       dto.setType("DELETE_ALL");
       messagingTemplate.convertAndSend("/topic/groups/" + groupId, dto);
    }
}
