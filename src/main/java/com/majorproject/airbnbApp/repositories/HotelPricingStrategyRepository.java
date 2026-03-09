package com.majorproject.airbnbApp.repositories;

import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.HotelPricingStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HotelPricingStrategyRepository extends JpaRepository<HotelPricingStrategy, Long> {

    // All active strategies for a hotel
    List<HotelPricingStrategy> findByHotelAndIsActiveTrue(Hotel hotel);

    // All records for a hotel (active + inactive)
    List<HotelPricingStrategy> findByHotel(Hotel hotel);

    // Check if a specific strategy is already linked to hotel
    Optional<HotelPricingStrategy> findByHotelAndPricingStrategyId(Hotel hotel, Long pricingStrategyId);

    // Get distinct hotels that have at least one active strategy — used by scheduler
    @Query("SELECT DISTINCT h.hotel FROM HotelPricingStrategy h WHERE h.isActive = true")
    List<Hotel> findDistinctHotelsWithActiveStrategy();
}