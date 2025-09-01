package com.majorproject.airbnbApp.dtos;

import com.majorproject.airbnbApp.entities.Guest;
import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.Room;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.entities.enums.BookingStatus;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {

    private Long id;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private Integer roomCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BookingStatus bookingStatus;
    private Set<GuestDto> guests;

}
