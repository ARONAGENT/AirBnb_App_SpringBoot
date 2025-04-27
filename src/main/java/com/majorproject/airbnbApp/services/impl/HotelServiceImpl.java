package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.dtos.HotelDto;
import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.repositories.HotelRepository;
import com.majorproject.airbnbApp.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
//@RequiredArgsConstructor automatically generates a constructor for all final fields.
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;

    @Override
    public HotelDto createHotel(HotelDto hotelDto) {
        log.info("Creating hotel with details: {}", hotelDto);
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotelRepository.save(hotel);
        log.info("Hotel created successfully with ID: {}", hotel.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto getHotelById(Long id) {
        log.info("Fetching hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found by ID: " + id));
        log.info("Hotel fetched successfully with ID: {}", hotel.getId());
        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        log.info("Updating hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found by ID: " + id));
        Hotel hotelToUpdate = modelMapper.map(hotelDto, Hotel.class);
        hotelToUpdate.setId(id);
        hotelRepository.save(hotelToUpdate);
        log.info("Hotel updated successfully with ID: {}", hotelToUpdate.getId());
        return modelMapper.map(hotelToUpdate, HotelDto.class);
    }

    @Override
    public HotelDto deleteHotelById(Long id) {
        log.info("Deleting hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found by ID: " + id));
        hotelRepository.delete(hotel);
        log.info("Hotel deleted successfully with ID: {}", id);
        return modelMapper.map(hotel, HotelDto.class);
    }
}
