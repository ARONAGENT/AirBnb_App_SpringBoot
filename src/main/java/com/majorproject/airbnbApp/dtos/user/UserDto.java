package com.majorproject.airbnbApp.dtos.user;


import com.majorproject.airbnbApp.entities.enums.Gender;
import com.majorproject.airbnbApp.entities.enums.Role;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@Data
public class UserDto implements Serializable {
    private Long id;
    private String email;
    private String name;
    private LocalDate dateOfBirth;
    private Set<Role> roles;   // ✅ FIXED
    private Gender gender;
}
