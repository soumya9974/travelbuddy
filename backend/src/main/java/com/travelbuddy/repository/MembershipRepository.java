package com.travelbuddy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.travelbuddy.model.Membership;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    List<Membership> findByGroupId(Long groupId);

    Optional<Membership> findByUserIdAndGroupId(Long userId, Long groupId);

	boolean existsByGroupIdAndUserId(Long groupId, Long userId);
	
	void deleteByUserIdAndGroupId(Long userId, Long groupId);

}
