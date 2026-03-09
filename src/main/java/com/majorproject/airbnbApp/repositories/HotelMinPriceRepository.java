package com.majorproject.airbnbApp.repositories;

import com.majorproject.airbnbApp.dtos.hotel.HotelPriceProjection;
import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.HotelMinPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface HotelMinPriceRepository extends JpaRepository<HotelMinPrice,Long> {

    @Query(value = """
    SELECT h.id, h.name, h.city, h.photos, h.amenities, h.active,
           AVG(hmp.price) as avg_price
    FROM hotel_min_price hmp
    JOIN hotel h ON h.id = hmp.hotel_id
    WHERE (:city IS NULL OR h.city = :city)
        AND (:startDate IS NULL OR hmp.date >= CAST(:startDate AS date))
        AND (:endDate IS NULL OR hmp.date <= CAST(:endDate AS date))
        AND h.active = true
    GROUP BY h.id, h.name, h.city, h.photos, h.amenities, h.active
    HAVING (:startDate IS NULL OR COUNT(hmp.date) >= :dateCount)
    """,
            countQuery = """
    SELECT COUNT(DISTINCT h.id)
    FROM hotel_min_price hmp
    JOIN hotel h ON h.id = hmp.hotel_id
    WHERE (:city IS NULL OR h.city = :city)
        AND (:startDate IS NULL OR hmp.date >= CAST(:startDate AS date))
        AND (:endDate IS NULL OR hmp.date <= CAST(:endDate AS date))
        AND h.active = true
    """,
            nativeQuery = true)
    Page<HotelPriceProjection> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
            @Param("roomsCount") Integer roomsCount,
            @Param("dateCount") Long dateCount,
            Pageable pageable
    );


    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);


    // Wipes all HotelMinPrice rows for a hotel — used when strategy changes
    void deleteByHotel(Hotel hotel);

    List<HotelMinPrice> findByHotelOrderByDateAsc(Hotel hotel);

}
