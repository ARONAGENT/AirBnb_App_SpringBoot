package com.majorproject.airbnbApp.controllers;

import com.majorproject.airbnbApp.dtos.MessageDto;
import com.majorproject.airbnbApp.dtos.strategyDto.CustomStrategyRequestDto;
import com.majorproject.airbnbApp.dtos.strategyDto.HolidayRequestDto;
import com.majorproject.airbnbApp.dtos.strategyDto.HotelStrategyUpdateDto;
import com.majorproject.airbnbApp.dtos.strategyDto.PricingStrategyResponseDto;
import com.majorproject.airbnbApp.entities.Holiday;
import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.HotelMinPrice;
import com.majorproject.airbnbApp.entities.User;
import com.majorproject.airbnbApp.exceptions.ResourceNotFoundException;
import com.majorproject.airbnbApp.exceptions.UnAuthorisedException;
import com.majorproject.airbnbApp.repositories.HotelMinPriceRepository;
import com.majorproject.airbnbApp.repositories.HotelRepository;
import com.majorproject.airbnbApp.services.HotelPricingStrategyService;
import com.majorproject.airbnbApp.services.impl.PricingUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.majorproject.airbnbApp.utils.AppUtils.getCurrentUser;
@Tag(
        name = "Hotel Pricing Strategy Management",
        description = "APIs for managing hotel pricing strategies, applying dynamic pricing, handling holidays, and retrieving pricing analytics."
)
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class HotelPricingStrategyController {

    private final HotelPricingStrategyService hotelPricingStrategyService;
    private final PricingUpdateService pricingUpdateService;
    private final HotelRepository hotelRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;

    // POST /admin/strategies/custom
    @PostMapping("/strategies/custom")
    @Operation(
            summary = "Create Custom Pricing Strategy",
            description = "Allows admin to create a custom pricing strategy that can later be assigned to hotels."
    )
    public ResponseEntity<PricingStrategyResponseDto> createCustomStrategy(
            @RequestBody CustomStrategyRequestDto dto) {
        return ResponseEntity.ok(hotelPricingStrategyService.createCustomStrategy(dto));
    }

    // GET /admin/strategies
    @GetMapping("/strategies")
    // GET /admin/strategies
    @Operation(
            summary = "Get Available Pricing Strategies",
            description = "Fetches all available pricing strategies that can be applied to hotels."
    )
    public ResponseEntity<List<PricingStrategyResponseDto>> getAvailableStrategies() {
        return ResponseEntity.ok(hotelPricingStrategyService.getAvailableStrategies());
    }

    // PUT /admin/hotels/{hotelId}/pricing-strategy
    @Operation(
            summary = "Update Hotel Pricing Strategies",
            description = "Assigns or updates pricing strategies for a specific hotel."
    )
    @PutMapping("/hotels/{hotelId}/pricing-strategy")
    public ResponseEntity<List<PricingStrategyResponseDto>> updateHotelStrategies(
            @PathVariable Long hotelId,
            @RequestBody HotelStrategyUpdateDto dto) {
        return ResponseEntity.ok(hotelPricingStrategyService.updateHotelStrategies(hotelId, dto));
    }

    // GET /admin/hotels/{hotelId}/pricing-strategy
    @Operation(
            summary = "Get Hotel Pricing Strategies",
            description = "Retrieves the pricing strategies currently applied to a specific hotel."
    )
    @GetMapping("/hotels/{hotelId}/pricing-strategy")
    public ResponseEntity<List<PricingStrategyResponseDto>> getHotelStrategies(
            @PathVariable Long hotelId) {
        return ResponseEntity.ok(hotelPricingStrategyService.getHotelStrategies(hotelId));
    }

    // POST /admin/hotels/{hotelId}/pricing/apply
    @Operation(
            summary = "Apply Pricing Strategy",
            description = "Triggers pricing calculation immediately for the specified hotel based on its assigned strategies."
    )
    @PostMapping("/hotels/{hotelId}/pricing/apply")
    public ResponseEntity<MessageDto> applyPricingNow(@PathVariable Long hotelId) {
        verifyHotelOwnership(hotelId);
        verifyHotelActive(hotelId);   // ✅ NEW
        pricingUpdateService.manualPricingUpdate(hotelId);
        return ResponseEntity.ok(new MessageDto("Pricing update started in background for hotel: " + hotelId));
    }

    // POST /admin/hotels/{hotelId}/pricing/reset
    @Operation(
            summary = "Reset and Recalculate Pricing",
            description = "Clears existing calculated prices and triggers a fresh recalculation for the hotel."
    )
    @PostMapping("/hotels/{hotelId}/pricing/reset")
    public ResponseEntity<MessageDto> resetAndRecalculatePricing(@PathVariable Long hotelId) {
        verifyHotelOwnership(hotelId);
        verifyHotelActive(hotelId);   // ✅ NEW

        pricingUpdateService.clearAndRecalculate(hotelId);
        return ResponseEntity.ok(new MessageDto("Old pricing cleared. Fresh recalculation started for hotel: " + hotelId));
    }

    // GET /admin/hotels/{hotelId}/min-prices  ← NEW: for the 90-day chart
    @Operation(
            summary = "Get Hotel Minimum Prices",
            description = "Returns the minimum room prices for a hotel across upcoming dates. Used for pricing charts or analytics."
    )
    @GetMapping("/hotels/{hotelId}/min-prices")
    public ResponseEntity<List<HotelMinPrice>> getMinPrices(@PathVariable Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));
        verifyHotelActive(hotelId);   // ✅ NEW
        return ResponseEntity.ok(hotelMinPriceRepository.findByHotelOrderByDateAsc(hotel));
    }

    // POST /admin/holidays
    @PostMapping("/holidays")
    @Operation(
            summary = "Add Holiday",
            description = "Adds a holiday date which can influence hotel pricing strategies such as surge pricing."
    )
    public ResponseEntity<Holiday> addHoliday(@RequestBody HolidayRequestDto dto) {
        return ResponseEntity.ok(hotelPricingStrategyService.saveHoliday(dto));
    }

    // ── Shared ownership check ─────────────────────────────────────────────
    private void verifyHotelOwnership(Long hotelId) {
        User currentUser = getCurrentUser();
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        if (!hotel.getOwner().getId().equals(currentUser.getId())) {
            throw new UnAuthorisedException("This user does not own the hotel with id: " + hotelId);
        }
    }

    private void verifyHotelActive(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + hotelId));
        if (!Boolean.TRUE.equals(hotel.getActive())) {
            throw new IllegalStateException(
                    "Hotel is not activated yet. Activate it first to initialize 365-day inventory " +
                            "before applying pricing strategies."
            );
        }
    }
}