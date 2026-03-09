package com.majorproject.airbnbApp.dtos.hotel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDate;

@Data
public class HotelSearchRequest {
    private String city;          // removed @NotBlank

    private LocalDate startDate;  // removed @NotNull

    private LocalDate endDate;    // removed @NotNull

    @Min(value = 1)
    private Integer roomsCount = 1; // default to 1, no @NotNull

    @Min(value = 0)
    private Integer page = 0;

    @Min(value = 1) @Max(value = 100)
    private Integer size = 10;


}
