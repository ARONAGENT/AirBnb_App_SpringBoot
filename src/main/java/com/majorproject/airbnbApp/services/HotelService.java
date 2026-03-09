package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.hotel.HotelDto;
import com.majorproject.airbnbApp.dtos.hotel.HotelInfoDto;

import java.util.List;

public interface HotelService {
    HotelDto createHotel(HotelDto hotelDto);
    HotelDto getHotelById(Long id);
    HotelDto updateHotelById(Long id, HotelDto hotelDto);
    void deleteHotelById(Long id);
    void activateHotel(Long id);

    HotelInfoDto getHotelInfoById(Long hotelId);

    List<HotelDto> getAllHotels();
}
