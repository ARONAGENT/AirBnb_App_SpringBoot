package com.majorproject.airbnbApp.repositories;

import com.majorproject.airbnbApp.entities.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    Optional<Holiday> findByDate(LocalDate date);
    boolean existsByDateAndIsHolidayTrue(LocalDate date);
}