package com.majorproject.airbnbApp.strategy;


import com.majorproject.airbnbApp.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

// Used for: BASED, custom strategies, and any strategy that is just a flat multiplier
@RequiredArgsConstructor
public class GenericMultiplierStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;
    private final BigDecimal multiplicationFactor;


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);
        return price.multiply(multiplicationFactor);
    }
}




