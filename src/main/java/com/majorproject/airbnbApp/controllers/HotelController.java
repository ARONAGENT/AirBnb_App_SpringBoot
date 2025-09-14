package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.BookingDto;
import com.majorproject.airbnbApp.dtos.HotelDto;
import com.majorproject.airbnbApp.dtos.HotelReportDto;
import com.majorproject.airbnbApp.services.BookingService;
import com.majorproject.airbnbApp.services.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
@Tag(name = "Hotel", description = "Admin APIs for managing hotels,bookings and their Reports")
@RestController
@RequestMapping(path = "admin/hotel")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @Operation(
            summary = "Create a new hotel",
            description = "Creates a new hotel with the provided details and returns the created hotel object"
    )
    @PostMapping("/create")
    public ResponseEntity<HotelDto> createHotel(@RequestBody @Valid HotelDto hotelDto) {
        log.info("Received request to create a hotel with details: {}", hotelDto);
        HotelDto createdHotel = hotelService.createHotel(hotelDto);
        log.info("Hotel created successfully with ID: {}", createdHotel.getId());
        return new ResponseEntity<>(createdHotel, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get a hotel by ID",
            description = "Fetches the details of a hotel using its ID"
    )
    @GetMapping("/get/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        log.info("Received request to fetch hotel with ID: {}", id);
        HotelDto hotelDto = hotelService.getHotelById(id);
        log.info("Fetched hotel with ID: {}", hotelDto.getId());
        return ResponseEntity.ok(hotelDto);
    }

    @Operation(
            summary = "Update a hotel by ID",
            description = "Updates the hotel details for the specified hotel ID and returns the updated hotel object"
    )
    @PutMapping("/update/{id}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long id, @RequestBody @Valid HotelDto hotelDto) {
        log.info("Received request to update hotel with ID: {} and details: {}", id, hotelDto);
        HotelDto updatedHotel = hotelService.updateHotelById(id, hotelDto);
        log.info("Updated hotel with ID: {}", updatedHotel.getId());
        return ResponseEntity.ok(updatedHotel);
    }

    @Operation(
            summary = "Delete a hotel by ID",
            description = "Deletes the specified hotel by ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long id) {
        log.info("Received request to delete hotel with ID: {}", id);
        hotelService.deleteHotelById(id);
        log.info("Hotel with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Activate a hotel by ID",
            description = "Activates the specified hotel, making it available for bookings"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long id) {
        log.info("Received request to activate hotel with ID: {}", id);
        hotelService.activateHotel(id);
        log.info("Hotel with ID: {} activated successfully", id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get all hotels",
            description = "Fetches a list of all hotels"
    )
    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @Operation(
            summary = "Get all bookings for a hotel",
            description = "Fetches all bookings associated with the specified hotel ID"
    )
    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookingsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByHotelId(hotelId));
    }

    @Operation(
            summary = "Get hotel report",
            description = "Fetches a report for the specified hotel within the given date range. Defaults to last 1 month if dates are not provided."
    )
    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId, startDate, endDate));
    }
}
