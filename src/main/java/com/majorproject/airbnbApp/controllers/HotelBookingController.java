package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.BookingDto;
import com.majorproject.airbnbApp.dtos.BookingRequest;
import com.majorproject.airbnbApp.dtos.GuestDto;
import com.majorproject.airbnbApp.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping("/init")
    public ResponseEntity<BookingDto> creatingBooking(@RequestBody BookingRequest bookingRequest){
      return  ResponseEntity.ok(bookingService.initializedBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
                                                @RequestBody List<GuestDto> guestIdList) {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestIdList));
    }

}
