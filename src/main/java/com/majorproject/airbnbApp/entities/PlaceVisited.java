package com.majorproject.airbnbApp.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "place_visited",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "place_name"})
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlaceVisited {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "place_name", nullable = false)
    private String placeName;   // e.g. city or hotel location

    @Column(name = "hotel_name")
    private String hotelName;

    @Column(name = "visited_at")
    private LocalDateTime visitedAt;

    @PrePersist
    public void setVisitedAt() {
        this.visitedAt = LocalDateTime.now();
    }
}