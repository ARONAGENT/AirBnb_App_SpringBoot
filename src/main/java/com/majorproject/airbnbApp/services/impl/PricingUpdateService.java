package com.majorproject.airbnbApp.services.impl;

import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.HotelMinPrice;
import com.majorproject.airbnbApp.entities.Inventory;
import com.majorproject.airbnbApp.repositories.HotelMinPriceRepository;
import com.majorproject.airbnbApp.repositories.HotelPricingStrategyRepository;
import com.majorproject.airbnbApp.repositories.HotelRepository;
import com.majorproject.airbnbApp.repositories.InventoryRepository;
import com.majorproject.airbnbApp.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PricingUpdateService {

    private final InventoryRepository inventoryRepository;
    private final HotelMinPriceRepository hotelMinPriceRepository;
    private final HotelPricingStrategyRepository hotelPricingStrategyRepository;
    private final HotelRepository hotelRepository;
    private final PricingService pricingService;

    // ── Every 30 days — wipe + recalculate all hotels ──────────────────────
    @Scheduled(cron = "0 0 0 1 */1 *")
    public void scheduledPricingUpdate() {
        log.info("30-day Pricing Scheduler STARTED");
        List<Hotel> hotels = hotelPricingStrategyRepository.findDistinctHotelsWithActiveStrategy();
        if (hotels.isEmpty()) {
            log.info("No hotels with active strategies. Skipping.");
            return;
        }
        hotels.forEach(this::clearAndRecalculateHotel);
        log.info("30-day Pricing Scheduler COMPLETED");
    }

    // ── Manual update within 30-day window (upsert, no wipe) ──────────────
    @Async
    public void manualPricingUpdate(Long hotelId) {
        log.info("Manual pricing update for hotel ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found: " + hotelId));
        buildHotelMinPrice(hotel);
        log.info("Manual pricing update completed for hotel ID: {}", hotelId);
    }

    // ── Strategy changed — wipe HotelMinPrice + rebuild from Inventory ─────
    @Async
    public void clearAndRecalculate(Long hotelId) {
        log.info("Clear & Recalculate for hotel ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new RuntimeException("Hotel not found: " + hotelId));
        clearAndRecalculateHotel(hotel);
        log.info("Clear & Recalculate completed for hotel ID: {}", hotelId);
    }

    // ── Core: wipe HotelMinPrice → read Inventory → apply strategy → save ──
    private void clearAndRecalculateHotel(Hotel hotel) {
        // Step 1: Wipe old HotelMinPrice data for this hotel
        log.info("Wiping HotelMinPrice for hotel ID: {}", hotel.getId());
        hotelMinPriceRepository.deleteByHotel(hotel);

        // Step 2: Rebuild fresh from Inventory base prices
        buildHotelMinPrice(hotel);
    }

    // ── Read Inventory (base prices) → apply strategy → save to HotelMinPrice ──
    // Inventory table is NEVER modified — it always holds base/normal prices only
    private void buildHotelMinPrice(Hotel hotel) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(90); // 90-day window

        // Read from Inventory — base prices only, never touch these
        List<Inventory> inventoryList = inventoryRepository
                .findByHotelAndDateBetween(hotel, startDate, endDate);

        if (inventoryList.isEmpty()) {
            log.info("No inventory found for hotel ID: {} in next 90 days.", hotel.getId());
            return;
        }

        // For each date — find the minimum dynamic price across all rooms
        // pricingService reads hotel's active strategy from DB and applies chain
        Map<LocalDate, BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(
                                // Apply dynamic pricing on top of base price — Inventory is NOT saved
                                pricingService::calculateDynamicPricing,
                                Collectors.minBy(Comparator.naturalOrder())
                        )
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().orElse(BigDecimal.ZERO)
                ));

        // Save calculated prices into HotelMinPrice only
        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date, price) -> {
            HotelMinPrice hotelMinPrice = hotelMinPriceRepository
                    .findByHotelAndDate(hotel, date)
                    .orElse(new HotelMinPrice(hotel, date));
            hotelMinPrice.setPrice(price);
            hotelPrices.add(hotelMinPrice);
        });

        hotelMinPriceRepository.saveAll(hotelPrices);
        log.info("HotelMinPrice updated for hotel ID: {} — {} days written.", hotel.getId(), hotelPrices.size());
    }
}