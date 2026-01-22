package com.travelbuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity @Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "memberships")
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    @JsonIgnore
    private TravelGroup group;

    private String role;
    private LocalDateTime joinedAt;
	
	@Override
	public String toString() {
		return "Membership [id=" + id + ", user=" + user + ", group=" + group + ", role=" + role + ", joinedAt="
				+ joinedAt + "]";
	}
}
