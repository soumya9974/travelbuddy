package com.travelbuddy.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketPresenceInterceptor implements ChannelInterceptor {

    private final SimpMessagingTemplate messagingTemplate;

    // groupId -> userIds
    private final Map<Long, Set<Long>> onlineUsers = new ConcurrentHashMap<>();

    // sessionId -> groupId
    private final Map<String, Long> sessionGroupMap = new ConcurrentHashMap<>();

    public WebSocketPresenceInterceptor(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        Principal principal = accessor.getUser();
        if (!(principal instanceof UsernamePasswordAuthenticationToken auth)) {
            return message;
        }

        Object principalValue = auth.getPrincipal();
        if (!(principalValue instanceof Long userId)) {
            return message;
        }

        String sessionId = accessor.getSessionId();

        // ---------- SUBSCRIBE ----------
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            String destination = accessor.getDestination();

            if (destination != null && destination.startsWith("/topic/groups/")) {

                Long groupId = extractGroupId(destination);

                sessionGroupMap.put(sessionId, groupId);

                onlineUsers
                        .computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet())
                        .add(userId);

                broadcast(groupId);
            }
        }

        // ---------- DISCONNECT ----------
        if (StompCommand.DISCONNECT.equals(accessor.getCommand())) {

            Long groupId = sessionGroupMap.remove(sessionId);

            if (groupId != null) {
                Set<Long> users = onlineUsers.get(groupId);
                if (users != null) {
                    users.remove(userId);
                    broadcast(groupId);
                }
            }
        }

        return message;
    }

    private void broadcast(Long groupId) {
        int onlineCount = onlineUsers
                .getOrDefault(groupId, Set.of())
                .size();

        messagingTemplate.convertAndSend(
                "/topic/groups/" + groupId + "/online",
                onlineCount
        );
    }


    private Long extractGroupId(String destination) {
        // /topic/groups/{id}
        String[] parts = destination.split("/");
        return Long.parseLong(parts[3]);
    }
}
