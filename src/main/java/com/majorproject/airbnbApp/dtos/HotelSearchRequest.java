package com.majorproject.airbnbApp.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequest {
    @NotBlank(message = "City is required")
    private String city;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Rooms count is required")
    @Min(value = 1, message = "Rooms count must be at least 1")
    private Integer roomsCount;

    // Pagination defaults
    @Min(value = 0, message = "Page index must be 0 or greater")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    private Integer size = 10;

}
