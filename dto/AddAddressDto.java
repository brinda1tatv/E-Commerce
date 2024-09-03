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
public class AddAddressDto {

    private int id;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String firstName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String lastName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^((91[0-9]{10}))$", message = "Please enter a valid phone number")
    @Size(min = 12, max = 12, message = "Your phone number must be consist of 10 numbers")
    private String phone;

    @NotNull(message = "This field must not be null")
    @NotBlank(message = "This field must not be blank")
    private String type;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Size(max = 200, message = "Your address must not exceed 200 words")
    private String address;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String city;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String state;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^\\d{6}$", message = "Please enter a valid zipcode")
    private String zipCode;

}
