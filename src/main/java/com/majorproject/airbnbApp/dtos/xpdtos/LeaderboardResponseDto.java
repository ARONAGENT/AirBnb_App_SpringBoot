package com.majorproject.airbnbApp.dtos.xpdtos;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor @Builder
public class LeaderboardResponseDto implements Serializable {

    private List<LeaderboardEntryDto> leaderboard;
    private String sortedBy;      // "xp" or "places"
}
