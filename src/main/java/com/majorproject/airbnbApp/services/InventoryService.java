package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.entities.Room;

public interface InventoryService {
    void initializedRoomForAYear(Room room);
    void deleteAllInventories(Room room);
}
