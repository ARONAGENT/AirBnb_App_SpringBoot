package com.majorproject.airbnbApp.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequest {
    private String city;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer roomsCount;

    // for pagination and pagenated result need page and size
    private Integer page=0;
    private Integer size=10;
}
