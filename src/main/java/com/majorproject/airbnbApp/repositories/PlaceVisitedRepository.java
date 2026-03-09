package com.majorproject.airbnbApp.repositories;

import com.majorproject.airbnbApp.entities.PlaceVisited;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaceVisitedRepository extends JpaRepository<PlaceVisited, Long> {

    List<PlaceVisited> findByUserId(Long userId);

    Optional<PlaceVisited> findByUserIdAndPlaceName(Long userId, String placeName);

    boolean existsByUserIdAndPlaceName(Long userId, String placeName);

    long countByUserId(Long userId);
}
