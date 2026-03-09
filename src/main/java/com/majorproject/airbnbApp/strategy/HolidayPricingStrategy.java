package com.majorproject.airbnbApp.strategy;

import com.majorproject.airbnbApp.entities.Inventory;
import com.majorproject.airbnbApp.repositories.HolidayRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;
    private final HolidayRepository holidayRepository;
    private final BigDecimal multiplicationFactor; // from DB (default 1.25)

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        boolean isHoliday = holidayRepository
                .existsByDateAndIsHolidayTrue(inventory.getDate());

        if (isHoliday) {
            price = price.multiply(multiplicationFactor);
        }
        return price;
    }
}