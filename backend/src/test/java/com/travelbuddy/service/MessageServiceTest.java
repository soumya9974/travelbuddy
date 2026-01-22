package com.travelbuddy.service;

import com.travelbuddy.model.Message;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.MessageRepository;
import com.travelbuddy.repository.TravelGroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock MessageRepository messageRepository;
    @Mock TravelGroupRepository travelGroupRepository;

    @InjectMocks MessageService messageService;

    @Test
    void saveMessage_shouldSetTimestamp_andPersist() {
        User user = new User();
        user.setId(1L);

        TravelGroup group = new TravelGroup();
        group.setId(9L);

        when(travelGroupRepository.findById(9L)).thenReturn(Optional.of(group));
        when(messageRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Message saved = messageService.saveMessage(9L, user, "Hello");

        assertNotNull(saved.getSentAt());
        assertEquals("Hello", saved.getContent());
        assertEquals(group, saved.getGroup());
        assertEquals(user, saved.getSender());
    }
}
