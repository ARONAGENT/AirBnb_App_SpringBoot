package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.config.XpConfig;
import com.majorproject.airbnbApp.dtos.xpdtos.LeaderboardEntryDto;
import com.majorproject.airbnbApp.dtos.xpdtos.LeaderboardResponseDto;
import com.majorproject.airbnbApp.dtos.xpdtos.MigrationResultDto;
import com.majorproject.airbnbApp.dtos.xpdtos.PlaceVisitedDto;
import com.majorproject.airbnbApp.dtos.xpdtos.XpResponseDto;
import com.majorproject.airbnbApp.entities.Booking;
import com.majorproject.airbnbApp.entities.PlaceVisited;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.entities.UserXp;
import com.majorproject.airbnbApp.entities.enums.BookingStatus;
import com.majorproject.airbnbApp.repositories.BookingRepository;
import com.majorproject.airbnbApp.repositories.PlaceVisitedRepository;
import com.majorproject.airbnbApp.repositories.UserRepository;
import com.majorproject.airbnbApp.repositories.UserXpRepository;
import com.majorproject.airbnbApp.services.XpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class XpServiceImpl implements XpService {

    // ── Cache name constants ────────────────────────────────────────────────
    // Keep all cache names here so they're easy to find and change
    static final String XP_PROFILE_CACHE    = "xp_profile";     // key = userId
    static final String XP_PLACES_CACHE     = "xp_places";      // key = userId
    static final String XP_LEADERBOARD_CACHE = "xp_leaderboard"; // key = sortBy ("xp" | "places")

    private final UserXpRepository userXpRepository;
    private final PlaceVisitedRepository placeVisitedRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final XpConfig xpConfig;

    // ----------------------------------------------------------------
    // Called internally from BookingServiceImpl after payment confirmed.
    // Evicts this user's profile + places cache, and the full leaderboard
    // (both sort variants) since rankings may have changed.
    // ----------------------------------------------------------------
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = XP_PROFILE_CACHE,     key = "#userId"),
            @CacheEvict(value = XP_PLACES_CACHE,      key = "#userId"),
            @CacheEvict(value = XP_LEADERBOARD_CACHE, key = "'xp'"),
            @CacheEvict(value = XP_LEADERBOARD_CACHE, key = "'places'"),
    })
    public XpResponseDto awardBookingXp(Long userId, String placeName, String hotelName) {
        UserXp userXp = getOrCreateUserXp(userId);
        int xpEarned = xpConfig.XP_PER_BOOKING;

        userXp.setBookingsCount(userXp.getBookingsCount() + 1);

        // Bonus XP if this is a new unique place
        boolean isNewPlace = !placeVisitedRepository.existsByUserIdAndPlaceName(userId, placeName);
        if (isNewPlace) {
            xpEarned += xpConfig.XP_PER_NEW_PLACE;
            userXp.setPlacesVisitedCount(userXp.getPlacesVisitedCount() + 1);

            placeVisitedRepository.save(PlaceVisited.builder()
                    .userId(userId)
                    .placeName(placeName)
                    .hotelName(hotelName)
                    .build());

            log.info("New place '{}' recorded for userId: {}", placeName, userId);
        }

        boolean leveledUp = updateUserXp(userXp, xpEarned);
        return buildXpResponse(userXp, xpEarned, leveledUp);
    }

    // ----------------------------------------------------------------
    // Migration: evicts everything since all users' data changes
    // ----------------------------------------------------------------
    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = XP_PROFILE_CACHE,     allEntries = true),
            @CacheEvict(value = XP_PLACES_CACHE,      allEntries = true),
            @CacheEvict(value = XP_LEADERBOARD_CACHE, allEntries = true),
    })
    public MigrationResultDto migrateExistingUsersXp() {
        log.info("Starting XP migration for existing users...");

        List<Booking> confirmedBookings = bookingRepository.findByBookingStatus(BookingStatus.CONFIRMED);

        int totalBookingsProcessed = 0;
        int skipped = 0;

        for (Booking booking : confirmedBookings) {
            Long userId = booking.getUser().getId();
            String placeName = booking.getHotel().getCity();
            String hotelName = booking.getHotel().getName();

            UserXp userXp = getOrCreateUserXp(userId);

            long totalConfirmed = bookingRepository.countByUserIdAndBookingStatus(userId, BookingStatus.CONFIRMED);
            if (userXp.getBookingsCount() >= totalConfirmed) {
                log.debug("Skipping already migrated userId: {}", userId);
                skipped++;
                continue;
            }

            int xpEarned = xpConfig.XP_PER_BOOKING;
            userXp.setBookingsCount(userXp.getBookingsCount() + 1);

            boolean isNewPlace = !placeVisitedRepository.existsByUserIdAndPlaceName(userId, placeName);
            if (isNewPlace) {
                xpEarned += xpConfig.XP_PER_NEW_PLACE;
                userXp.setPlacesVisitedCount(userXp.getPlacesVisitedCount() + 1);

                placeVisitedRepository.save(PlaceVisited.builder()
                        .userId(userId)
                        .placeName(placeName)
                        .hotelName(hotelName)
                        .build());
            }

            updateUserXp(userXp, xpEarned);
            totalBookingsProcessed++;
        }

        log.info("XP migration complete. Processed: {}, Skipped: {}", totalBookingsProcessed, skipped);

        return MigrationResultDto.builder()
                .totalBookingsProcessed(totalBookingsProcessed)
                .skipped(skipped)
                .message("XP migration completed successfully!")
                .build();
    }

    // ----------------------------------------------------------------
    // Per-user XP profile cache — key is the logged-in user's ID
    // Cached after first call; evicted by awardBookingXp for that userId
    // ----------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = XP_PROFILE_CACHE, key = "#root.target.getCurrentUser().id")
    public XpResponseDto getUserXpProfile() {
        User user = getCurrentUser();
        UserXp userXp = getOrCreateUserXp(user.getId());
        return buildXpResponse(userXp, 0, false);
    }

    // ----------------------------------------------------------------
    // Per-user places cache — key is the logged-in user's ID
    // Cached after first call; evicted by awardBookingXp for that userId
    // ----------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = XP_PLACES_CACHE, key = "#root.target.getCurrentUser().id")
    public List<PlaceVisitedDto> getPlacesVisited() {
        User user = getCurrentUser();
        return placeVisitedRepository.findByUserId(user.getId())
                .stream()
                .map(p -> PlaceVisitedDto.builder()
                        .id(p.getId())
                        .placeName(p.getPlaceName())
                        .hotelName(p.getHotelName())
                        .visitedAt(p.getVisitedAt() != null ? p.getVisitedAt().toString() : null)
                        .build())
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // Shared leaderboard cache — key is sortBy ("xp" or "places")
    // Same for all users. Evicted whenever any booking is confirmed.
    // ----------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = XP_LEADERBOARD_CACHE, key = "#sortBy")
    public LeaderboardResponseDto getLeaderboard(String sortBy) {
        List<UserXp> ranked = sortBy.equalsIgnoreCase("places")
                ? userXpRepository.findTopByPlacesVisited()
                : userXpRepository.findTopByXp();

        // Batch-fetch all user names in one query — avoids N+1
        List<Long> userIds = ranked.stream().map(UserXp::getUserId).collect(Collectors.toList());
        Map<Long, String> nameByUserId = userRepository.findAllById(userIds)
                .stream()
                .collect(Collectors.toMap(User::getId, User::getName));

        List<LeaderboardEntryDto> entries = IntStream.range(0, ranked.size())
                .mapToObj(i -> {
                    UserXp u = ranked.get(i);
                    return LeaderboardEntryDto.builder()
                            .rank(i + 1)
                            .userId(u.getUserId())
                            .userName(nameByUserId.getOrDefault(u.getUserId(), "Traveler #" + u.getUserId()))
                            .totalXp(u.getTotalXp())
                            .level(u.getLevel())
                            .rankTitle(xpConfig.getRankTitle(u.getLevel()))
                            .placesVisitedCount(u.getPlacesVisitedCount())
                            .build();
                })
                .collect(Collectors.toList());

        return LeaderboardResponseDto.builder()
                .leaderboard(entries)
                .sortedBy(sortBy)
                .build();
    }

    // ----------------------------------------------------------------
    // Private helpers
    // ----------------------------------------------------------------

    private boolean updateUserXp(UserXp userXp, int xpEarned) {
        int oldLevel = userXp.getLevel();

        userXp.setTotalXp(userXp.getTotalXp() + xpEarned);
        int newLevel = xpConfig.calculateLevel(userXp.getTotalXp());
        userXp.setLevel(newLevel);
        userXpRepository.save(userXp);

        boolean leveledUp = newLevel > oldLevel;
        if (leveledUp) {
            log.info("UserId: {} leveled up from {} to {}!", userXp.getUserId(), oldLevel, newLevel);
        }
        return leveledUp;
    }

    private UserXp getOrCreateUserXp(Long userId) {
        return userXpRepository.findByUserId(userId)
                .orElseGet(() -> userXpRepository.save(
                        UserXp.builder()
                                .userId(userId)
                                .totalXp(0)
                                .level(1)
                                .placesVisitedCount(0)
                                .bookingsCount(0)
                                .build()
                ));
    }

    // public so SpEL (#root.target.getCurrentUser().id) can access it from @Cacheable
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private XpResponseDto buildXpResponse(UserXp userXp, int xpEarned, boolean leveledUp) {
        return XpResponseDto.builder()
                .userId(userXp.getUserId())
                .totalXp(userXp.getTotalXp())
                .level(userXp.getLevel())
                .rankTitle(xpConfig.getRankTitle(userXp.getLevel()))
                .placesVisitedCount(userXp.getPlacesVisitedCount())
                .bookingsCount(userXp.getBookingsCount())
                .xpEarned(xpEarned)
                .leveledUp(leveledUp)
                .build();
    }
}