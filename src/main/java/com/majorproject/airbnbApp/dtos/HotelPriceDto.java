package com.majorproject.airbnbApp.dtos;

import com.majorproject.airbnbApp.entities.Hotel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelPriceDto implements Serializable {

    private Hotel hotel;
    private Double price;
}
