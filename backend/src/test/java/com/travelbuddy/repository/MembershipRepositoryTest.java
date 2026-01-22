package com.travelbuddy.repository;

import com.travelbuddy.model.Membership;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class MembershipRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired TravelGroupRepository travelGroupRepository;
    @Autowired MembershipRepository membershipRepository;

    @Test
    void existsByGroupIdAndUserId_trueWhenPresent() {
        User u = new User();
        u.setName("Bob");
        u.setUsername("bob");
        u.setEmail("bob@example.com");
        u.setPasswordHash("hash");
        u = userRepository.save(u);

        TravelGroup g = new TravelGroup();
        g.setName("Trip");
        g.setDestination("Rome");
        g.setDescription("desc");
        g.setStartDate(LocalDate.of(2026, 3, 1));
        g.setEndDate(LocalDate.of(2026, 3, 5));
        g.setCreatedBy(u); // required (nullable=false)
        g = travelGroupRepository.save(g);

        Membership m = new Membership();
        m.setUser(u);
        m.setGroup(g);
        m.setRole("MEMBER");
        m.setJoinedAt(LocalDateTime.now());
        membershipRepository.save(m);

        assertTrue(membershipRepository.existsByGroupIdAndUserId(g.getId(), u.getId()));
        assertFalse(membershipRepository.existsByGroupIdAndUserId(g.getId(), 999L));
    }
}
