package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.HotelDto;
import com.majorproject.airbnbApp.dtos.HotelSearchRequest;
import com.majorproject.airbnbApp.entities.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {
    void initializedRoomForAYear(Room room);
    void deleteAllInventories(Room room);

    Page<HotelDto> searchHotels(HotelSearchRequest hotelSearchRequest);
}
