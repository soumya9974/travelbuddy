package com.travelbuddy.model;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity @Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "itineraries")
public class Itinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private TravelGroup group;

    @Column(name = "day_number", nullable = false)
    private int day;

    private String title;

    @Column(length = 1000)
    private String description;

    private String location;

    private LocalTime startTime;

    private LocalTime endTime;

    private LocalDateTime createdAt;

	@Override
	public String toString() {
		return "Itinerary [id=" + id + ", group=" + group + ", day=" + day + ", title=" + title + ", description="
				+ description + ", location=" + location + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", createdAt=" + createdAt + "]";
	}
}

