package com.majorproject.airbnbApp.dtos.strategyDto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class HolidayRequestDto {
    private LocalDate date;
    private String name;
    private Boolean isHoliday;
}