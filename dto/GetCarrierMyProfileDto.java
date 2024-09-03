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
public class GetCarrierMyProfileDto {

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String firstName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String lastName;

    private String email;

    private String role;

    private String phone;

    @NotNull(message = "This field must not be null")
    private int gender;

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
