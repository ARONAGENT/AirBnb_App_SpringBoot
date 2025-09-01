package com.majorproject.airbnbApp.dtos;


import com.majorproject.airbnbApp.entities.Booking;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.entities.enums.Gender;
import lombok.Data;

import java.util.Set;

@Data
public class GuestDto {
    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;
    private Set<Booking> bookings;
}
