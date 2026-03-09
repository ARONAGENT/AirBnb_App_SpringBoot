package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.dtos.hotel.HotelDto;
import com.majorproject.airbnbApp.dtos.hotel.HotelPriceDto;
import com.majorproject.airbnbApp.dtos.hotel.HotelPriceProjection;
import com.majorproject.airbnbApp.dtos.hotel.HotelSearchRequest;
import com.majorproject.airbnbApp.dtos.roomAndInventory.InventoryDto;
import com.majorproject.airbnbApp.dtos.roomAndInventory.UpdateInventoryRequestDto;
import com.majorproject.airbnbApp.entities.Inventory;
import com.majorproject.airbnbApp.entities.Room;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.repositories.HotelMinPriceRepository;
import com.majorproject.airbnbApp.repositories.InventoryRepository;
import com.majorproject.airbnbApp.repositories.RoomRepository;
import com.majorproject.airbnbApp.services.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.majorproject.airbnbApp.utils.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final RoomRepository roomRepository;

    @Override
    public void initializedRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);

        List<Inventory> inventories = new ArrayList<>();

        for (; !today.isAfter(endDate); today = today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .reservedCount(0)
                    .city(room.getHotel().getCity())
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .date(today)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();
            inventories.add(inventory);
        }

        inventoryRepository.saveAll(inventories);
    }
    @Override
    public void deleteAllInventories(Room room) {
        inventoryRepository.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotels(HotelSearchRequest req) {
        LocalDate start = req.getStartDate();
        LocalDate end   = req.getEndDate();

        String startStr = start != null ? start.toString() : null; // "2026-03-19"
        String endStr   = end   != null ? end.toString()   : null;

        long dateCount = (start != null && end != null)
                ? ChronoUnit.DAYS.between(start, end) + 1 : 0L;

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 10
        );

        String city = (req.getCity() != null && !req.getCity().isBlank()) ? req.getCity() : null;

        Page<HotelPriceProjection> raw = hotelMinPriceRepository.findHotelsWithAvailableInventory(
                city, startStr, endStr,
                req.getRoomsCount() != null ? req.getRoomsCount() : 1,
                dateCount, pageable
        );

        return raw.map(row -> {
            HotelDto hotelDto = new HotelDto();
            hotelDto.setId(row.getId());
            hotelDto.setName(row.getName());
            hotelDto.setCity(row.getCity());
            hotelDto.setPhotos(row.getPhotos());
            hotelDto.setAmenities(row.getAmenities());
            hotelDto.setActive(row.getActive());
            return new HotelPriceDto(hotelDto, row.getAvgPrice());
        });
    }


    @Override
    public List<InventoryDto> getAllInventoryByRoom(Long roomId) {
        log.info("Getting all inventory for room ID: {}", roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        User user = getCurrentUser();

        if (!user.equals(room.getHotel().getOwner())) {
            throw new AccessDeniedException("You are not the owner of room with id: " + roomId);
        }

        return inventoryRepository.findByRoomOrderByDate(room)
                .stream()
                .map(element -> modelMapper.map(element, InventoryDto.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto) {
        log.info("Updating inventory for room ID: {} between {} - {}",
                roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate());

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

        User user = getCurrentUser();

        if (!user.equals(room.getHotel().getOwner())) {
            throw new AccessDeniedException("You are not the owner of room with id: " + roomId);
        }

        inventoryRepository.getInventoryAndLockBeforeUpdate(
                roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate());

        inventoryRepository.updateInventory(
                roomId,
                updateInventoryRequestDto.getStartDate(),
                updateInventoryRequestDto.getEndDate(),
                updateInventoryRequestDto.getClosed(),
                updateInventoryRequestDto.getSurgeFactor());
    }

    @Override
    public Page<HotelPriceDto> searchAllHotels() {
        return null;
    }
}