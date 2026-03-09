package com.majorproject.airbnbApp.dtos.strategyDto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomStrategyRequestDto {
    private String strategyName;           // e.g. "MY_WEEKEND_BOOST"
    private BigDecimal multiplicationFactor; // e.g. 1.30 = 30% increase
}