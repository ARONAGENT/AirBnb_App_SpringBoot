package com.majorproject.airbnbApp.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateInventoryRequestDto {
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Surge factor is required")
    @DecimalMin(value = "1", message = "Surge factor must be >= 1")
    private BigDecimal surgeFactor;

    @NotNull(message = "Closed flag is required")
    private Boolean closed;
}
