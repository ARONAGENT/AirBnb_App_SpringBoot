package com.majorproject.airbnbApp.repositories;

import com.majorproject.airbnbApp.entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest,Long> {
}
