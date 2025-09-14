package com.majorproject.airbnbApp.repositories;

import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long> {
    List<Hotel> findByOwner(User user);
}
