package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.strategyDto.CustomStrategyRequestDto;
import com.majorproject.airbnbApp.dtos.strategyDto.HolidayRequestDto;
import com.majorproject.airbnbApp.dtos.strategyDto.HotelStrategyUpdateDto;
import com.majorproject.airbnbApp.dtos.strategyDto.PricingStrategyResponseDto;
import com.majorproject.airbnbApp.entities.Holiday;

import java.util.List;

public interface HotelPricingStrategyService {

    PricingStrategyResponseDto createCustomStrategy(CustomStrategyRequestDto dto);

    List<PricingStrategyResponseDto> getAvailableStrategies();

    // Ownership of hotel verified internally
    List<PricingStrategyResponseDto> updateHotelStrategies(Long hotelId, HotelStrategyUpdateDto dto);

    List<PricingStrategyResponseDto> getHotelStrategies(Long hotelId);

    Holiday saveHoliday(HolidayRequestDto dto);
}
