package com.majorproject.airbnbApp.services;

import com.majorproject.airbnbApp.entities.Booking;

public interface CheckoutService {
    String getCheckOutSession(Booking booking, String successUrl, String failureUrl);
}
