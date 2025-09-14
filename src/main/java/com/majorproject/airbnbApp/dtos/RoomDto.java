package com.majorproject.airbnbApp.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomDto {

    private Long id;

    @NotBlank(message = "Room type is required")
    private String type;

    @NotNull(message = "Base price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Base price must be greater than 0")
    private BigDecimal basePrice;

    private String[] photos;
    private String[] amenities;

    @NotNull(message = "Total count is required")
    @Min(value = 1, message = "Total count must be at least 1")
    private Integer totalCount;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}

