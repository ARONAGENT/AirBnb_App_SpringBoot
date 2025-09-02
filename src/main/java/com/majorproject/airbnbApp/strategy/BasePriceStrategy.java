package com.majorproject.airbnbApp.strategy;

import com.majorproject.airbnbApp.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
@RequiredArgsConstructor
public class BasePriceStrategy implements PricingStrategy{
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        return inventory.getRoom().getBasePrice();
    }
}
