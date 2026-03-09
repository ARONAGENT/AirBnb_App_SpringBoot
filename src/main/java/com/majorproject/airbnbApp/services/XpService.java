package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.xpdtos.LeaderboardResponseDto;
import com.majorproject.airbnbApp.dtos.xpdtos.MigrationResultDto;
import com.majorproject.airbnbApp.dtos.xpdtos.PlaceVisitedDto;
import com.majorproject.airbnbApp.dtos.xpdtos.XpResponseDto;

import java.util.List;

public interface XpService {
    // Called internally from BookingServiceImpl after payment confirmed
    XpResponseDto awardBookingXp(Long userId, String placeName, String hotelName);

    // One-click backfill for all old confirmed bookings
    MigrationResultDto migrateExistingUsersXp();

    // Called from controller — resolves current user internally
    XpResponseDto getUserXpProfile();

    // Called from controller — resolves current user internally
    List<PlaceVisitedDto> getPlacesVisited();

    // Public leaderboard — no user context needed
    LeaderboardResponseDto getLeaderboard(String sortBy);
}
