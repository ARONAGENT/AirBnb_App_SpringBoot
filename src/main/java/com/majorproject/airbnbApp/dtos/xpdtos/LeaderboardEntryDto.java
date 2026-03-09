package com.majorproject.airbnbApp.dtos.xpdtos;

import lombok.*;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class LeaderboardEntryDto implements Serializable {
    private int rank;
    private Long userId;
    private String userName;        // ✅ fetched from User entity
    private int totalXp;
    private int level;
    private String rankTitle;
    private int placesVisitedCount;
}
