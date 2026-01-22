package com.travelbuddy.websocket;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PresenceRegistry {

    // groupId -> set of userIds
    private final Map<Long, Set<Long>> onlineUsersByGroup = new ConcurrentHashMap<>();

    public void userJoined(Long groupId, Long userId) {
        onlineUsersByGroup
                .computeIfAbsent(groupId, k -> ConcurrentHashMap.newKeySet())
                .add(userId);
    }

    public void userLeft(Long groupId, Long userId) {
        Set<Long> users = onlineUsersByGroup.get(groupId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                onlineUsersByGroup.remove(groupId);
            }
        }
    }

    public Set<Long> getOnlineUsers(Long groupId) {
        return onlineUsersByGroup.getOrDefault(groupId, Set.of());
    }
}
