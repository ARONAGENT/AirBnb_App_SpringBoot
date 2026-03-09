package com.majorproject.airbnbApp.dtos.hotel;

import com.majorproject.airbnbApp.dtos.roomAndInventory.RoomDto;
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
