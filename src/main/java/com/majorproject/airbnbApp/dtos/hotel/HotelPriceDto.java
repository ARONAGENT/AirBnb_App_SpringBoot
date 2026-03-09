package com.majorproject.airbnbApp.dtos.hotel;

import com.majorproject.airbnbApp.entities.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDto implements Serializable {

    private HotelDto hotel;
    private Double price;

    // Constructor that accepts Hotel entity — used by JPQL SELECT new
    public HotelPriceDto(Hotel hotel, Double price) {
        this.price = price;
        this.hotel = new HotelDto();
        this.hotel.setId(hotel.getId());
        this.hotel.setName(hotel.getName());
        this.hotel.setCity(hotel.getCity());
        this.hotel.setPhotos(hotel.getPhotos());
        this.hotel.setAmenities(hotel.getAmenities());
        this.hotel.setContactInfo(hotel.getContactInfo());
        this.hotel.setActive(hotel.getActive());
    }
}
