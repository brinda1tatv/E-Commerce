package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditPersonalInfoDto {

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String firstName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String lastName;

    private String email;

    private String role;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^((91[0-9]{10}))$", message = "Please enter a valid phone number")
    @Size(min = 12, max = 12, message = "Your phone number must be consist of 10 numbers")
    private String phone;

    @NotNull(message = "This field must not be null")
    private int gender;

}
