package com.majorproject.airbnbApp.entities;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class HotelContactInfo {
    private String address;
    private String location;
    private String email;
    private String phoneNumber;

}

