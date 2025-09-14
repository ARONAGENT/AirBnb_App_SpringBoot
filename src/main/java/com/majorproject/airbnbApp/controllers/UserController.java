package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.BookingDto;
import com.majorproject.airbnbApp.dtos.GuestDto;
import com.majorproject.airbnbApp.dtos.ProfileUpdateRequestDto;
import com.majorproject.airbnbApp.dtos.UserDto;
import com.majorproject.airbnbApp.services.BookingService;
import com.majorproject.airbnbApp.services.GuestService;
import com.majorproject.airbnbApp.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "User", description = "APIs for User")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final BookingService bookingService;
    private final GuestService guestService;

    @PatchMapping("/profile")
    @Operation(summary = "Update user profile", description = "Updates the profile details of the currently logged-in user")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        userService.updateProfile(profileUpdateRequestDto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    @Operation(summary = "Get my bookings", description = "Fetches a list of all bookings made by the current user")
    public ResponseEntity<List<BookingDto>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/profile")
    @Operation(summary = "Get my profile", description = "Fetches the profile details of the currently logged-in user")
    public ResponseEntity<UserDto> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @GetMapping("/guests")
    @Operation(summary = "Get all guests", description = "Fetches all guests linked to the current user")
    public ResponseEntity<List<GuestDto>> getAllGuests() {
        return ResponseEntity.ok(guestService.getAllGuests());
    }

    @PostMapping("/guests")
    @Operation(summary = "Add new guest", description = "Adds a new guest profile for the current user")
    public ResponseEntity<GuestDto> addNewGuest(@RequestBody GuestDto guestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(guestService.addNewGuest(guestDto));
    }

    @PutMapping("guests/{guestId}")
    @Operation(summary = "Update guest", description = "Updates the details of an existing guest by guest ID")
    public ResponseEntity<Void> updateGuest(@PathVariable Long guestId, @RequestBody GuestDto guestDto) {
        guestService.updateGuest(guestId, guestDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("guests/{guestId}")
    @Operation(summary = "Delete guest", description = "Deletes an existing guest by guest ID")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.noContent().build();
    }
}
