package com.majorproject.airbnbApp.dtos;

import com.majorproject.airbnbApp.annotations.PasswordChecker;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @PasswordChecker
    private String password;
}
