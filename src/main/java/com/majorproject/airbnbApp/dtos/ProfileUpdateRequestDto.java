package com.majorproject.airbnbApp.dtos;

import com.majorproject.airbnbApp.entities.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDto {

    @NotNull(message = "name is required")
    @NotBlank(message = "name cannot be blank")
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}
