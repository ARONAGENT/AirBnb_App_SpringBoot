package com.majorproject.airbnbApp.dtos.strategyDto;

import lombok.Data;

import java.util.List;

@Data
public class HotelStrategyUpdateDto {
    private List<Long> strategyIds; // IDs from pricing_strategy table
}