package com.majorproject.airbnbApp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.majorproject.airbnbApp.entities.HotelContactInfo;
import lombok.Data;

@Data
public class HotelDto {
    private Long id;
    private String name;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean Active;
}
