package com.travelbuddy.repository;

import com.travelbuddy.model.Itinerary;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ItineraryRepositoryTest {

    @Autowired UserRepository userRepository;
    @Autowired TravelGroupRepository travelGroupRepository;
    @Autowired ItineraryRepository itineraryRepository;

    @Test
    void findByGroupIdOrderByDayAscStartTimeAsc_ordersCorrectly() {
        User u = new User();
        u.setName("Owner");
        u.setUsername("owner");
        u.setEmail("owner@example.com");
        u.setPasswordHash("hash");
        u = userRepository.save(u);

        TravelGroup g = new TravelGroup();
        g.setName("Plan");
        g.setDestination("Berlin");
        g.setDescription("desc");
        g.setStartDate(LocalDate.of(2026, 4, 1));
        g.setEndDate(LocalDate.of(2026, 4, 10));
        g.setCreatedBy(u);
        g = travelGroupRepository.save(g);

        Itinerary i1 = new Itinerary();
        i1.setGroup(g);
        i1.setDay(1);
        i1.setTitle("Late");
        i1.setStartTime(LocalTime.of(11, 0));
        itineraryRepository.save(i1);

        Itinerary i2 = new Itinerary();
        i2.setGroup(g);
        i2.setDay(1);
        i2.setTitle("Early");
        i2.setStartTime(LocalTime.of(9, 0));
        itineraryRepository.save(i2);

        Itinerary i3 = new Itinerary();
        i3.setGroup(g);
        i3.setDay(2);
        i3.setTitle("Day2");
        i3.setStartTime(LocalTime.of(8, 0));
        itineraryRepository.save(i3);

        List<Itinerary> ordered = itineraryRepository.findByGroupIdOrderByDayAscStartTimeAsc(g.getId());
        assertEquals(3, ordered.size());
        assertEquals("Early", ordered.get(0).getTitle());
        assertEquals("Late", ordered.get(1).getTitle());
        assertEquals("Day2", ordered.get(2).getTitle());
    }
}
