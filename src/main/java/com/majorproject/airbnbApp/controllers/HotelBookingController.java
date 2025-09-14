package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.BookingDto;
import com.majorproject.airbnbApp.dtos.BookingRequest;
import com.majorproject.airbnbApp.dtos.GuestDto;
import com.majorproject.airbnbApp.services.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@Tag(name = "Booking", description = "APIs for managing hotel bookings")
@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;
    @Operation(
            summary = "Create a new booking",
            description = "Initializes a new booking with the provided booking request details"
    )
    @PostMapping("/init")
    public ResponseEntity<BookingDto> creatingBooking(@RequestBody BookingRequest bookingRequest) {
        return ResponseEntity.ok(bookingService.initializedBooking(bookingRequest));
    }

    @Operation(
            summary = "Add guests to an existing booking",
            description = "Adds a list of guests to the specified booking ID"
    )
    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId,
                                                @RequestBody List<GuestDto> guestIdList) {
        return ResponseEntity.ok(bookingService.addGuests(bookingId, guestIdList));
    }

    @Operation(
            summary = "Initiate payment for a booking",
            description = "Starts the payment process for the given booking ID and returns a session URL for payment gateway"
    )
    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String, String>> initiatePayments(@PathVariable Long bookingId) {
        String sessionUrl = bookingService.initiatePayments(bookingId);
        return ResponseEntity.ok(Map.of("sessionUrl", sessionUrl));
    }

    @Operation(
            summary = "Cancel a booking",
            description = "Cancels the specified booking using its booking ID"
    )
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBookings(@PathVariable Long bookingId) {
        bookingService.cancelBooking(bookingId);
        return ResponseEntity.noContent().build();
    }
//    @GetMapping("/{bookingId}/status")
//    public ResponseEntity<BookingStatusResponseDto> getBookingStatus(@PathVariable Long bookingId) {
//        return ResponseEntity.ok(new BookingStatusResponseDto(bookingService.getBookingStatus(bookingId)));
//    }

}
