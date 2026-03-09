package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.*;
import com.majorproject.airbnbApp.dtos.booking.BookingDto;
import com.majorproject.airbnbApp.dtos.booking.GuestDto;
import com.majorproject.airbnbApp.dtos.user.ProfileUpdateRequestDto;
import com.majorproject.airbnbApp.dtos.user.UserDto;
import com.majorproject.airbnbApp.services.AiService;
import com.majorproject.airbnbApp.services.BookingService;
import com.majorproject.airbnbApp.services.GuestService;
import com.majorproject.airbnbApp.services.UserService;
import com.majorproject.airbnbApp.services.impl.AiServiceImpl;
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

    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "Api for Ai Ask Ai", description = "Gives Info about what u want just like chatgpt")
    public ResponseEntity<MessageDto> getAnalysisOfReports(@RequestBody String Msg){
        String ans=aiService.askAi(Msg);
        System.out.println(ans);
        return ResponseEntity.ok(new MessageDto(ans));
    }

    @PatchMapping("/profile")
    @Operation(summary = "Update user profile", description = "Updates the profile details of the currently logged-in user")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDto profileUpdateRequestDto) {
        userService.updateProfile(profileUpdateRequestDto);
        System.out.println(profileUpdateRequestDto.getName());
        System.out.println(profileUpdateRequestDto.getDateOfBirth());
        System.out.println(profileUpdateRequestDto.getGender());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/myBookings")
    @Operation(summary = "Get my bookings", description = "Fetches a list of all bookings made by the current user")
    public ResponseEntity<List<BookingDto>> getMyBookings() {
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/bookings/{bookingId}")
    @Operation(summary = "Get my bookings By Id", description = "Fetches a bookings by ID made by the current user")
    public ResponseEntity<BookingDto> getMyBookingsByID(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getMyBookingsById(bookingId));
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

    @GetMapping("/guests/{guestId}")
    @Operation(summary = "Get all guests", description = "Fetches all guests linked to the current user")
    public ResponseEntity<GuestDto> getGuestById(@PathVariable Long guestId) {
        return ResponseEntity.ok(guestService.getGuestById(guestId));
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
