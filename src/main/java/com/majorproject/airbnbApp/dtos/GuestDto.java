package com.majorproject.airbnbApp.dtos;


import com.majorproject.airbnbApp.entities.Booking;
import com.majorproject.airbnbApp.entities.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestDto {
    private Long id;
    private String name;
    private Gender gender;
    private Integer age;
}
