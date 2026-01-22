package com.travelbuddy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity @Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // full name

    @Column(unique = true, nullable = false)
    private String username; // NEW

    @Column(unique = true, nullable = false)
    private String email;
    
    @Transient
    private String password;

    @Column(nullable = false)
    private String passwordHash;

    private String bio;
    private String profilePicUrl;
    private boolean verificationStatus;

    private LocalDateTime createdAt;
    
    private String resetToken;
    private LocalDateTime resetTokenExpiry;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Membership> memberships;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Message> messages;

	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", username=" + username + ", email=" + email + ", password="
				+ password + ", passwordHash=" + passwordHash + ", bio=" + bio + ", profilePicUrl=" + profilePicUrl
				+ ", verificationStatus=" + verificationStatus + ", createdAt=" + createdAt + ", resetToken="
				+ resetToken + ", resetTokenExpiry=" + resetTokenExpiry + "]";
	}
    
}

