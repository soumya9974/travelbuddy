package com.travelbuddy.repository;

import com.travelbuddy.model.TravelGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelGroupRepository extends JpaRepository<TravelGroup, Long> {
	
}

