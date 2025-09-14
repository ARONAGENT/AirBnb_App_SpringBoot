package com.majorproject.airbnbApp.dtos;

import com.majorproject.airbnbApp.annotations.PasswordChecker;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SignUpRequestDto {

    @NotNull(message = "name field cannot be null")
    @NotBlank(message = "Name should not be Empty ")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @PasswordChecker
    private String password;
}
