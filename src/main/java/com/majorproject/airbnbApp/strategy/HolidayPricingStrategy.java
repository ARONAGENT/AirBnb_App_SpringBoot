package com.majorproject.airbnbApp.strategy;

import com.majorproject.airbnbApp.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy{

    private  final PricingStrategy wrapped;
    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
      BigDecimal price= wrapped.calculatePrice(inventory);
      boolean isTodayHoliday = true; // call the api for actual holiday or not
      if(isTodayHoliday){
          price=price.multiply(BigDecimal.valueOf(1.25));
      }
        return price;
    }
}
