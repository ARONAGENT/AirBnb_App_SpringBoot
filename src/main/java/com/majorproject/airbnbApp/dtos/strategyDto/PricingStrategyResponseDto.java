package com.majorproject.airbnbApp.dtos.strategyDto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PricingStrategyResponseDto {
    private Long id;
    private String strategyName;
    private BigDecimal multiplicationFactor;
    private Boolean isDefault;
    private Boolean isActive;
}