package com.majorproject.airbnbApp.strategy;

import com.majorproject.airbnbApp.entities.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
