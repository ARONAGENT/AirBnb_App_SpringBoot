package com.majorproject.airbnbApp.dtos;


import com.majorproject.airbnbApp.entities.enums.Gender;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class UserDto implements Serializable {
    private Long id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}
