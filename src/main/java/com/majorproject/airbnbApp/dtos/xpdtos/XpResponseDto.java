package com.majorproject.airbnbApp.dtos.xpdtos;

import lombok.*;

import java.io.Serializable;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class XpResponseDto implements Serializable {
    private Long userId;
    private int totalXp;
    private int level;
    private String rankTitle;
    private int placesVisitedCount;
    private int bookingsCount;
    private int xpEarned;         // XP earned in this action
    private boolean leveledUp;
}
