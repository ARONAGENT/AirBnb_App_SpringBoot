package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.xpdtos.LeaderboardResponseDto;
import com.majorproject.airbnbApp.dtos.xpdtos.MigrationResultDto;
import com.majorproject.airbnbApp.dtos.xpdtos.PlaceVisitedDto;
import com.majorproject.airbnbApp.dtos.xpdtos.XpResponseDto;
import com.majorproject.airbnbApp.services.XpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(
        name = "XP Management API",
        description = "API for managing user engagement through XP (Experience Points). XP is calculated based on users' confirmed bookings, and levels are assigned according to the accumulated XP."
)@RestController
@RequiredArgsConstructor
@RequestMapping("/xp")
public class XpController {

    private final XpService xpService;

    @Operation(
            summary = "Get User XP Profile",
            description = "Retrieves the XP profile of the currently authenticated user, including total XP earned and current level."
    )
    @GetMapping("/profile")
    public ResponseEntity<XpResponseDto> getMyProfile() {
        return ResponseEntity.ok(xpService.getUserXpProfile());
    }

    @Operation(
            summary = "Get Places Visited by User",
            description = "Returns the list of places the user has visited based on confirmed bookings."
    )
    @GetMapping("/places")
    public ResponseEntity<List<PlaceVisitedDto>> getMyPlacesVisited() {
        return ResponseEntity.ok(xpService.getPlacesVisited());
    }

    @Operation(
            summary = "Get XP Leaderboard",
            description = "Fetches the leaderboard of users ranked by XP or other specified criteria."
    )
    @GetMapping("/leaderboard")
    public ResponseEntity<LeaderboardResponseDto> getLeaderboard(
            @RequestParam(defaultValue = "xp") String sortBy) {
        return ResponseEntity.ok(xpService.getLeaderboard(sortBy));
    }
//    /**
//     * POST /api/xp/migrate
//     * One-click backfill: awards XP to all existing users based on their confirmed bookings.
//     * Safe to call multiple times — already-processed users are skipped.
//     * Tip: secure this endpoint to ADMIN role in your SecurityConfig.
//     */
//    @PostMapping("/migrate")
//    public ResponseEntity<MigrationResultDto> migrateExistingUsers() {
//        return ResponseEntity.ok(xpService.migrateExistingUsersXp());
//    }

}
