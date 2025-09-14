package com.majorproject.airbnbApp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.majorproject.airbnbApp.entities.HotelContactInfo;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class HotelDto {
    private Long id;

    @NotNull(message = "name field cannot be null")
    @NotBlank(message = "Name should not be Empty ")
    private String name;

    @NotBlank(message = "city should not be Empty ")
    @NotNull(message = "city field cannot be null")
    private String city;

    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;

    @AssertFalse(message = "Initially isActive must be false when creating a new Hotel")
    private Boolean Active;
}
