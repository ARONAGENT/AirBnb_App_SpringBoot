package com.majorproject.airbnbApp.repositories;

import com.majorproject.airbnbApp.entities.Guest;
import com.majorproject.airbnbApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuestRepository extends JpaRepository<Guest,Long> {
    List<Guest> findByUser(User user);
}
