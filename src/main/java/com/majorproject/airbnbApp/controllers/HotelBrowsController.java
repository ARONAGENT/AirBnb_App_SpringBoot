package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.HotelDto;
import com.majorproject.airbnbApp.dtos.HotelInfoDto;
import com.majorproject.airbnbApp.dtos.HotelPriceDto;
import com.majorproject.airbnbApp.dtos.HotelSearchRequest;
import com.majorproject.airbnbApp.services.HotelService;
import com.majorproject.airbnbApp.services.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Hotel Searching", description = "APIs for Searching hotels")
@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowsController {


    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @Operation(
            summary = "Search hotels based on criteria",
            description = "Searches for hotels using the provided search criteria and returns a paginated list of hotel prices"
    )
    @PostMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotels(@RequestBody @Valid HotelSearchRequest hotelSearchRequest) {
        Page<HotelPriceDto> page = inventoryService.searchHotels(hotelSearchRequest);
        return ResponseEntity.ok(page);
    }

    @Operation(
            summary = "Get hotel information by ID",
            description = "Fetches detailed information for a specific hotel using its hotel ID (ADMIN Only)"
    )
    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
