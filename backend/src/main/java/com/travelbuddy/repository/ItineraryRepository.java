package com.travelbuddy.repository;

import com.travelbuddy.model.Itinerary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItineraryRepository extends JpaRepository<Itinerary, Long> {
	List<Itinerary> findByGroupIdOrderByDayAscStartTimeAsc(Long groupId);
}

