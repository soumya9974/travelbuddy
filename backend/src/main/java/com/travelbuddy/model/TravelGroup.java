package com.travelbuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity @Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "travel_groups")
public class TravelGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String destination;

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Membership> memberships;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Message> messages;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    private List<Itinerary> itineraries;

	@Override
	public String toString() {
		return "TravelGroup [id=" + id + ", name=" + name + ", description=" + description + ", destination="
				+ destination + ", startDate=" + startDate + ", endDate=" + endDate + ", createdBy=" + createdBy
				+ ", memberships=" + memberships + ", messages=" + messages + ", itineraries=" + itineraries + "]";
	}
}

