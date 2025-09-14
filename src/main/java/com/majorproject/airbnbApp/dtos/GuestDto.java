package com.majorproject.airbnbApp.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GuestDto {
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Gender is required")
    @Pattern(
            regexp = "^(MALE|FEMALE|OTHER)$",
            message = "Gender must be either MALE, FEMALE, or OTHER"
    )
    private String gender;

    @NotNull(message = "Age is required")
    @Min(value = 0, message = "Age must be 0 or greater")
    @Max(value = 120, message = "Age must be realistic (<= 120)")
    private Integer age;
}
