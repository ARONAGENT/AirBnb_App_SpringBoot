package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.BookingDto;
import com.majorproject.airbnbApp.dtos.BookingRequest;
import com.majorproject.airbnbApp.dtos.GuestDto;

import java.util.List;

public interface BookingService {

    BookingDto initializedBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestIdList);
}
