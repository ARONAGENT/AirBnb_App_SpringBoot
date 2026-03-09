package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.dtos.hotel.HotelDto;
import com.majorproject.airbnbApp.dtos.hotel.HotelInfoDto;
import com.majorproject.airbnbApp.dtos.roomAndInventory.RoomDto;
import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.Room;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.exceptions.UnAuthorisedException;
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
import java.util.stream.Collectors;

import static com.majorproject.airbnbApp.utils.AppUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;

    @Override
    public HotelDto createHotel(HotelDto hotelDto) {
        User user = getCurrentUser();
        Hotel hotel = modelMapper.map(hotelDto, Hotel.class);
        hotel.setOwner(user);
        hotel = hotelRepository.save(hotel);
        return modelMapper.map(hotel, HotelDto.class);
    }


    @Override
    public HotelDto getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found by ID: " + id));

        User user = getCurrentUser();

        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: " + id);
        }

        return modelMapper.map(hotel, HotelDto.class);
    }

    @Override
    public HotelDto updateHotelById(Long id, HotelDto hotelDto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found by ID: " + id));

        User user = getCurrentUser();

        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: " + id);
        }

        modelMapper.map(hotelDto, hotel);
        hotel = hotelRepository.save(hotel);

        return modelMapper.map(hotel, HotelDto.class);
    }
    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found by ID: " + id));

        User user = getCurrentUser();

        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: " + id);
        }

        for (Room room : hotel.getRooms()) {
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }

        hotelRepository.delete(hotel);
    }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + id));

        User user = getCurrentUser();

        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: " + id);
        }

        hotel.setActive(true);

        for (Room room : hotel.getRooms()) {
            inventoryService.initializedRoomForAYear(room);
        }
    }

    @Override
//    @Cacheable(value = "hotelInfo", key = "#hotelId")
    public HotelInfoDto getHotelInfoById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .toList();

        return new HotelInfoDto(modelMapper.map(hotel, HotelDto.class), rooms);
    }


    @Override
    public List<HotelDto> getAllHotels() {
        User user = getCurrentUser();

        return hotelRepository.findByOwner(user)
                .stream()
                .map(hotel -> modelMapper.map(hotel, HotelDto.class))
                .collect(Collectors.toList());
    }
}