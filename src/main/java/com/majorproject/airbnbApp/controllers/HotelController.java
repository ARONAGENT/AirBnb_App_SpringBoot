package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.BookingDto;
import com.majorproject.airbnbApp.dtos.HotelDto;
import com.majorproject.airbnbApp.dtos.HotelReportDto;
import com.majorproject.airbnbApp.services.BookingService;
import com.majorproject.airbnbApp.services.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "admin/hotel")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<HotelDto> createHotel(@RequestBody @Valid HotelDto hotelDto) {
        log.info("Received request to create a hotel with details: {}", hotelDto);
        HotelDto createdHotel = hotelService.createHotel(hotelDto);
        log.info("Hotel created successfully with ID: {}", createdHotel.getId());
        return new ResponseEntity<>(createdHotel,HttpStatus.CREATED);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long id) {
        log.info("Received request to fetch hotel with ID: {}", id);
        HotelDto hotelDto = hotelService.getHotelById(id);
        log.info("Fetched hotel with ID: {}", hotelDto.getId());
        return ResponseEntity.ok(hotelDto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable Long id, @RequestBody @Valid HotelDto hotelDto) {
        log.info("Received request to update hotel with ID: {} and details: {}", id, hotelDto);
        HotelDto updatedHotel = hotelService.updateHotelById(id, hotelDto);
        log.info("Updated hotel with ID: {}", updatedHotel.getId());
        return ResponseEntity.ok(updatedHotel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HotelDto> deleteHotelById(@PathVariable Long id) {
        log.info("Received request to delete hotel with ID: {}", id);
        hotelService.deleteHotelById(id);
        log.info("Hotel with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> activateHotel(@PathVariable Long id){
        log.info("Received request to active hotel with ID: {}", id);
        hotelService.activateHotel(id);
        log.info("Hotel with ID: {} active successfully", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookingsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByHotelId(hotelId));
    }

    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId, startDate, endDate));
    }
}
