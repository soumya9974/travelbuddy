package com.travelbuddy.repository;

import com.travelbuddy.model.Message;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MessageRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired TravelGroupRepository travelGroupRepository;
    @Autowired MessageRepository messageRepository;

    @Test
    void findByGroupIdOrderBySentAtAsc_ordersCorrectly() {
        User u = new User();
        u.setName("Owner");
        u.setUsername("owner2");
        u.setEmail("owner2@example.com");
        u.setPasswordHash("hash");
        u = userRepository.save(u);

        TravelGroup g = new TravelGroup();
        g.setName("Chat");
        g.setDestination("Paris");
        g.setDescription("desc");
        g.setStartDate(LocalDate.of(2026, 5, 1));
        g.setEndDate(LocalDate.of(2026, 5, 2));
        g.setCreatedBy(u);
        g = travelGroupRepository.save(g);

        Message m1 = new Message();
        m1.setGroup(g);
        m1.setSender(u);
        m1.setContent("first");
        m1.setSentAt(LocalDateTime.now().minusMinutes(2));
        messageRepository.save(m1);

        Message m2 = new Message();
        m2.setGroup(g);
        m2.setSender(u);
        m2.setContent("second");
        m2.setSentAt(LocalDateTime.now().minusMinutes(1));
        messageRepository.save(m2);

        List<Message> ordered = messageRepository.findByGroupIdOrderBySentAtAsc(g.getId());
        assertEquals(2, ordered.size());
        assertEquals("first", ordered.get(0).getContent());
        assertEquals("second", ordered.get(1).getContent());
    }

    @Test
    void deleteByGroupId_removesMessages() {
        User u = new User();
        u.setName("Owner3");
        u.setUsername("owner3");
        u.setEmail("owner3@example.com");
        u.setPasswordHash("hash");
        u = userRepository.save(u);

        TravelGroup g = new TravelGroup();
        g.setName("Chat2");
        g.setDestination("Rome");
        g.setDescription("desc");
        g.setStartDate(LocalDate.of(2026, 6, 1));
        g.setEndDate(LocalDate.of(2026, 6, 2));
        g.setCreatedBy(u);
        g = travelGroupRepository.save(g);

        Message m = new Message();
        m.setGroup(g);
        m.setSender(u);
        m.setContent("x");
        m.setSentAt(LocalDateTime.now());
        messageRepository.save(m);

        assertEquals(1, messageRepository.findByGroupId(g.getId()).size());
        messageRepository.deleteByGroupId(g.getId());
        assertEquals(0, messageRepository.findByGroupId(g.getId()).size());
    }
}
