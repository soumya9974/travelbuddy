package com.travelbuddy.service;

import com.travelbuddy.exception.ResourceNotFoundException;
import com.travelbuddy.model.Itinerary;
import com.travelbuddy.model.Membership;
import com.travelbuddy.model.TravelGroup;
import com.travelbuddy.model.User;
import com.travelbuddy.repository.ItineraryRepository;
import com.travelbuddy.repository.MembershipRepository;
import com.travelbuddy.repository.TravelGroupRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItineraryService {

    private final ItineraryRepository itineraryRepository;
    private final TravelGroupRepository travelGroupRepository;
    private final MembershipRepository membershipRepository;

    public ItineraryService(
            ItineraryRepository itineraryRepository,
            TravelGroupRepository travelGroupRepository,
            MembershipRepository membershipRepository
    ) {
        this.itineraryRepository = itineraryRepository;
        this.travelGroupRepository = travelGroupRepository;
        this.membershipRepository = membershipRepository;
    }

    // ---------------- GET ITINERARIES BY GROUP ----------------
    public List<Itinerary> getItinerariesByGroup(Long groupId) {

        travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        return itineraryRepository
                .findByGroupIdOrderByDayAscStartTimeAsc(groupId);
    }

    // ---------------- GET ALL ITINERARIES ----------------
    public List<Itinerary> getAllItineraries() {
        return itineraryRepository.findAll();
    }

    // ---------------- GET BY ID ----------------
    public Itinerary getItineraryById(Long id) {
        return itineraryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Itinerary not found with id: " + id));
    }

    // ---------------- CREATE ----------------
    public Itinerary createItinerary(Long groupId, Itinerary itinerary, User user) {

        membershipRepository.findByUserIdAndGroupId(user.getId(), groupId)
                .orElseThrow(() -> new RuntimeException("Only group members can add itineraries"));

        TravelGroup group = travelGroupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found"));

        itinerary.setGroup(group);
        itinerary.setCreatedAt(LocalDateTime.now());

        return itineraryRepository.save(itinerary);
    }

    // ---------------- UPDATE ----------------
    public Itinerary updateItinerary(Long id, Itinerary updated, User user) {

        Itinerary existing = getItineraryById(id);
        validateAdmin(user, existing.getGroup().getId());

        existing.setDay(updated.getDay());
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setLocation(updated.getLocation());
        existing.setStartTime(updated.getStartTime());
        existing.setEndTime(updated.getEndTime());

        return itineraryRepository.save(existing);
    }

    // ---------------- DELETE ----------------
    public void deleteItinerary(Long id, User user) {

        Itinerary itinerary = getItineraryById(id);
        validateAdmin(user, itinerary.getGroup().getId());

        itineraryRepository.delete(itinerary);
    }

    // ---------------- ADMIN CHECK ----------------
    private void validateAdmin(User user, Long groupId) {

        Membership membership = membershipRepository
                .findByUserIdAndGroupId(user.getId(), groupId)
                .orElseThrow(() -> new RuntimeException("Not a group member"));

        if (!"ADMIN".equals(membership.getRole())) {
            throw new RuntimeException("Only admins can modify itineraries");
        }
    }
}
