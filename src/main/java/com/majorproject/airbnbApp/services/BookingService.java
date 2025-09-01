package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.dtos.BookingDto;
import com.majorproject.airbnbApp.dtos.BookingRequest;

public interface BookingService {

    BookingDto initializedBooking(BookingRequest bookingRequest);
}
