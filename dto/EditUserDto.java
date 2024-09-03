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
public class EditUserDto {

    @NotNull(message = "This field must not be null")
    private int id;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Please enter characters only.")
    private String firstName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Please enter characters only.")
    private String lastName;

    @NotNull(message = "This field must not be null")
    private int role;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^((91[0-9]{10}))$", message = "Please enter a valid phone number")
    @Size(min = 12, max = 12, message = "Your phone number must be consist of 10 numbers")
    private String phone;

}
