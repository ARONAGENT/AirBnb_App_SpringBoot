package com.majorproject.airbnbApp.repositories;

import com.majorproject.airbnbApp.entities.UserXp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserXpRepository extends JpaRepository<UserXp,Long> {
    Optional<UserXp> findByUserId(Long userId);

    // Leaderboard: top N users by XP
    @Query("SELECT u FROM UserXp u ORDER BY u.totalXp DESC")
    List<UserXp> findTopByXp();

    // Leaderboard: top N users by places visited
    @Query("SELECT u FROM UserXp u ORDER BY u.placesVisitedCount DESC")
    List<UserXp> findTopByPlacesVisited();
}
