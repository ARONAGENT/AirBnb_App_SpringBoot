package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.dtos.roomAndInventory.RoomDto;
import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.Room;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.exceptions.UnAuthorisedException;
import com.majorproject.airbnbApp.repositories.HotelRepository;
import com.majorproject.airbnbApp.repositories.RoomRepository;
import com.majorproject.airbnbApp.services.InventoryService;
import com.majorproject.airbnbApp.services.RoomService;
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
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final InventoryService inventoryService;
    private final ModelMapper modelMapper;


    @Override
    public RoomDto createNewRoom(Long hotelId, RoomDto roomDto) {
        log.info("Creating new room in hotel ID: {}", hotelId);

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        User user = getCurrentUser();

        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: " + hotelId);
        }

        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);

        if (hotel.getActive()) {
            inventoryService.initializedRoomForAYear(room);
        }

        log.info("Room created successfully. Room ID: {}, Hotel ID: {}", room.getId(), hotelId);

        return modelMapper.map(room, RoomDto.class);
    }

    /* ============================================================
       GET ALL ROOMS IN HOTEL (owner-scoped)
       ============================================================ */
    @Override
    public List<RoomDto> getAllRoomsInHotel(Long hotelId) {
        log.info("Fetching all rooms for hotel ID: {}", hotelId);

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        User user = getCurrentUser();

        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: " + hotelId);
        }

        List<RoomDto> rooms = hotel.getRooms()
                .stream()
                .map(room -> modelMapper.map(room, RoomDto.class))
                .collect(Collectors.toList());

        log.info("Total {} rooms fetched for hotel ID: {}", rooms.size(), hotelId);

        return rooms;
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        log.info("Fetching room with ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        return modelMapper.map(room, RoomDto.class);
    }

    /* ============================================================
       DELETE ROOM
       Evict all related room + inventory caches
       ============================================================ */
    @Transactional
    @Override
    public void deleteRoomById(Long roomId) {
        log.info("Deleting room with ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        User user = getCurrentUser();

        if (!user.getId().equals(room.getHotel().getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with room id: " + roomId);
        }

        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);

        log.info("Room deleted successfully with ID: {}", roomId);
    }

    /* ============================================================
       UPDATE ROOM — NO @CachePut (safer to evict and let DB reload)
       ============================================================ */
    @Override
    @Transactional
    public RoomDto updateRoomById(Long hotelId, Long roomId, RoomDto roomDto) {
        log.info("Updating room ID: {} in hotel ID: {}", roomId, hotelId);

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: " + hotelId));

        User user = getCurrentUser();

        if (!user.getId().equals(hotel.getOwner().getId())) {
            throw new UnAuthorisedException("This user does not own this hotel with id: " + hotelId);
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        modelMapper.map(roomDto, room);
        room = roomRepository.save(room);

        log.info("Room updated successfully. Room ID: {}", roomId);

        return modelMapper.map(room, RoomDto.class);
    }
}