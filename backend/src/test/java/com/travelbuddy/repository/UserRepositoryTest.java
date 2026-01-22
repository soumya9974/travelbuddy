package com.travelbuddy.repository;

import com.travelbuddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@org.springframework.test.context.ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired UserRepository userRepository;

    @Test
    void findByEmail_returnsUser() {
        User u = new User();
        u.setName("Alice");
        u.setUsername("alice");
        u.setEmail("alice@example.com");
        u.setPasswordHash("hash");
        userRepository.save(u);

        Optional<User> found = userRepository.findByEmail("alice@example.com");
        assertTrue(found.isPresent());
        assertEquals("alice", found.get().getUsername());
    }
}
