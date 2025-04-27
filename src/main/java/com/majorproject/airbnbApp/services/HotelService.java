package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.HotelDto;

public interface HotelService {
    HotelDto createHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    HotelDto updateHotelById(Long id, HotelDto hotelDto);
    HotelDto deleteHotelById(Long id);

}
