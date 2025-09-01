package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.dtos.HotelDto;
import com.majorproject.airbnbApp.dtos.HotelInfoDto;
import com.majorproject.airbnbApp.dtos.RoomDto;
import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.Room;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.repositories.HotelRepository;
import com.majorproject.airbnbApp.repositories.RoomRepository;
import com.majorproject.airbnbApp.services.HotelService;
import com.majorproject.airbnbApp.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
//@RequiredArgsConstructor automatically generates a constructor for all final fields.
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;

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
    @Transactional
    public void deleteHotelById(Long id) {
        log.info("Deleting hotel with ID: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found by ID: " + id));
        for(Room room:hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.delete(hotel);
        log.info("Hotel deleted successfully with ID: {}", id);
        modelMapper.map(hotel, HotelDto.class);
    }

    @Transactional
    @Override
    public void activateHotel(Long id) {
        log.info("Activating the hotel with ID: {}", id);
        Hotel hotel = hotelRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+id));

        hotel.setActive(true);
        // create inventory for hotel
        for(Room room:hotel.getRooms()){
            inventoryService.initializedRoomForAYear(room);
        }

    }

    @Override
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel = hotelRepository
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        List<RoomDto> rooms=hotel.getRooms()
                .stream()
                .map((element) ->modelMapper.map(element,RoomDto.class))
                .toList();

        return new HotelInfoDto(modelMapper.map(hotel,HotelDto.class),rooms);
    }
}
