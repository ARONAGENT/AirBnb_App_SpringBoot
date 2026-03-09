package com.majorproject.airbnbApp.dtos.hotel;

public interface HotelPriceProjection {
    Long getId();
    String getName();
    String getCity();
    String[] getPhotos();
    String[] getAmenities();
    Boolean getActive();
    Double getAvgPrice();
}
