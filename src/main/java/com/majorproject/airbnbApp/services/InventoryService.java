package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.*;
import com.majorproject.airbnbApp.entities.Room;
import org.springframework.data.domain.Page;

import java.util.List;

public interface InventoryService {
    void initializedRoomForAYear(Room room);
    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotels(HotelSearchRequest hotelSearchRequest);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
