package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.GuestDto;

import java.util.List;

public interface GuestService {
    List<GuestDto> getAllGuests();

    void updateGuest(Long guestId, GuestDto guestDto);

    void deleteGuest(Long guestId);

    GuestDto addNewGuest(GuestDto guestDto);

    GuestDto getGuestById(Long guestId);
}
