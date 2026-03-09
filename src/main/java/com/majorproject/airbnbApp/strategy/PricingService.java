package com.majorproject.airbnbApp.strategy;

import com.majorproject.airbnbApp.entities.HotelPricingStrategy;
import com.majorproject.airbnbApp.entities.Inventory;
import com.majorproject.airbnbApp.repositories.HolidayRepository;
import com.majorproject.airbnbApp.repositories.HotelPricingStrategyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final HotelPricingStrategyRepository hotelPricingStrategyRepository;
    private final HolidayRepository holidayRepository;

    public BigDecimal calculateDynamicPricing(Inventory inventory) {
        Long hotelId = inventory.getRoom().getHotel().getId();

        // Always start with base price
        PricingStrategy chain = new BasePriceStrategy();

        // Fetch all active strategies for this hotel from DB
        List<HotelPricingStrategy> hotelStrategies = hotelPricingStrategyRepository
                .findByHotelAndIsActiveTrue(inventory.getRoom().getHotel());

        if (hotelStrategies.isEmpty()) {
            log.warn("No active strategies for hotel ID: {}. Returning base price.", hotelId);
            return chain.calculatePrice(inventory);
        }

        // Build decorator chain dynamically from DB strategies
        for (HotelPricingStrategy hotelStrategy : hotelStrategies) {
            String name = hotelStrategy.getPricingStrategy().getStrategyName().toUpperCase();
            BigDecimal factor = hotelStrategy.getPricingStrategy().getMultiplicationFactor();

            chain = switch (name) {
                // Special strategies — have their own logic beyond just multiplying
                case "SURGE"     -> new SurgePricingStrategy(chain);
                case "OCCUPANCY" -> new OccupancyPricingStrategy(chain);
                case "URGENCY"   -> new UrgencyPricingStrategy(chain);
                case "HOLIDAY"   -> new HolidayPricingStrategy(chain, holidayRepository, factor);

                // BASED or any custom strategy — just apply the multiplication factor
                default          -> new GenericMultiplierStrategy(chain, factor);
            };
        }

        return chain.calculatePrice(inventory);
    }

    // Used in booking flow — total price across multiple inventory dates
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList) {
        return inventoryList.stream()
                .map(this::calculateDynamicPricing)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}