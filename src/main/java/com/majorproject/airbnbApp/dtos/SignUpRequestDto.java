package com.majorproject.airbnbApp.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignUpRequestDto {

    @NotNull(message = "name field cannot be null")
    @NotBlank(message = "Name should not be Empty ")
    private String name;

    private String email;
    private String password;
}
