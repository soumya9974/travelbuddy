package com.travelbuddy.service;

import com.travelbuddy.model.Message;

import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.MessageRepository;
import com.travelbuddy.repository.TravelGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final TravelGroupRepository travelGroupRepository;

    public MessageService(
            MessageRepository messageRepository,
            TravelGroupRepository travelGroupRepository
    ) {
        this.messageRepository = messageRepository;
        this.travelGroupRepository = travelGroupRepository;
    }

    // ---------------- GET CHAT HISTORY ----------------
    public List<Message> getMessagesByGroup(Long groupId) {
        return messageRepository.findByGroupIdOrderBySentAtAsc(groupId);
    }
    
    // ----------------- DELETE MESSAGE -----------------
    @Transactional
    public void deleteMessageById(Long messageId) {
        messageRepository.deleteById(messageId);
    }
    // -------------DELETE ALL MESSAGES BY ADMIN -------------
    @Transactional
    public void deleteAllMessagesByGroupId(Long groupId) {
    	messageRepository.deleteByGroupId(groupId);
    }
    
    public Message getMessage(Long messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }

    // ---------------- SAVE MESSAGE ----------------
    public Message saveMessage(Long groupId, User sender, String content) {

        TravelGroup group = travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        Message message = new Message();
        message.setGroup(group);
        message.setSender(sender);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());

        return messageRepository.save(message);
    }
}
