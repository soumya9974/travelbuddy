package com.travelbuddy.repository;

import com.travelbuddy.model.Message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
	List<Message> findByGroupId(Long groupId);
	List<Message> findByGroupIdOrderBySentAtAsc(Long groupId);
	void deleteByGroupId(Long groupId);
}
