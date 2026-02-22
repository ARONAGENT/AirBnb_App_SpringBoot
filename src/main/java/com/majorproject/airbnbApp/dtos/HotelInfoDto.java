package com.majorproject.airbnbApp.dtos;

import com.majorproject.airbnbApp.entities.Hotel;
import com.majorproject.airbnbApp.entities.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelInfoDto implements Serializable {

    private HotelDto hotel;
    private List<RoomDto> rooms;
}
