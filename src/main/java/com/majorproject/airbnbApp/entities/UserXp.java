package com.majorproject.airbnbApp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_xp")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserXp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to your existing User entity
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "total_xp", nullable = false)
    private int totalXp = 0;

    @Column(name = "level", nullable = false)
    private int level = 1;

    @Column(name = "places_visited_count", nullable = false)
    private int placesVisitedCount = 0;

    @Column(name = "bookings_count", nullable = false)
    private int bookingsCount = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }
}