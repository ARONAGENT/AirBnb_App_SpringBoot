package com.majorproject.airbnbApp.strategy;

import com.majorproject.airbnbApp.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class OccupancyPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price=wrapped.calculatePrice(inventory);
        double occupancyRate= (double) inventory.getBookedCount() /inventory.getTotalCount();
        if(occupancyRate>0.8){
            price=price.multiply(BigDecimal.valueOf(1.2));
        }
        return price;
    }
}
