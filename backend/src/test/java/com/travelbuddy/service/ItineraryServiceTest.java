package com.travelbuddy.service;

import com.travelbuddy.exception.ResourceNotFoundException;
import com.travelbuddy.model.Itinerary;
import com.travelbuddy.model.Membership;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.ItineraryRepository;
import com.travelbuddy.repository.MembershipRepository;
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
class ItineraryServiceTest {

    @Mock ItineraryRepository itineraryRepository;
    @Mock TravelGroupRepository travelGroupRepository;
    @Mock MembershipRepository membershipRepository;

    @InjectMocks ItineraryService itineraryService;

    @Test
    void updateItinerary_notFound_shouldThrowResourceNotFound() {
        when(itineraryRepository.findById(1L)).thenReturn(Optional.empty());

        User user = new User();
        user.setId(1L);

        assertThrows(ResourceNotFoundException.class,
                () -> itineraryService.updateItinerary(1L, new Itinerary(), user));
    }

    @Test
    void createItinerary_shouldSave_whenUserIsMember() {
        long groupId = 1L;

        User user = new User();
        user.setId(1L);

        Membership membership = new Membership();
        membership.setRole("MEMBER");

        when(membershipRepository.findByUserIdAndGroupId(1L, groupId)).thenReturn(Optional.of(membership));

        TravelGroup group = new TravelGroup();
        group.setId(groupId);
        when(travelGroupRepository.findById(groupId)).thenReturn(Optional.of(group));

        Itinerary itinerary = new Itinerary();
        itinerary.setTitle("Day 1");

        when(itineraryRepository.save(any(Itinerary.class))).thenAnswer(i -> i.getArgument(0));

        Itinerary result = itineraryService.createItinerary(groupId, itinerary, user);

        assertEquals("Day 1", result.getTitle());
        assertNotNull(result.getCreatedAt());
        assertEquals(group, result.getGroup());
        verify(itineraryRepository).save(any(Itinerary.class));
    }

    @Test
    void createItinerary_shouldThrow_whenUserNotMember() {
        long groupId = 1L;

        User user = new User();
        user.setId(1L);

        when(membershipRepository.findByUserIdAndGroupId(1L, groupId)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itineraryService.createItinerary(groupId, new Itinerary(), user));

        assertEquals("Only group members can add itineraries", ex.getMessage());
        verify(itineraryRepository, never()).save(any());
    }
}
