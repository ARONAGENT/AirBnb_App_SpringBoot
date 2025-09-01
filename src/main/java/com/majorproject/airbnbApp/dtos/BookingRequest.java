package com.majorproject.airbnbApp.dtos;

import lombok.Data;

import java.time.LocalDate;


@Data
public class BookingRequest {

    private Long hotelId;
    private Long RoomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomsCount;
}
