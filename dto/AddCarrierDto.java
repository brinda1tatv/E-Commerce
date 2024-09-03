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
public class AddCarrierDto {

    private int carrierId;

    private int userId;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Please enter characters only.")
    private String firstName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Please enter characters only.")
    private String lastName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String gender;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^((91[0-9]{10}))$", message = "Please enter a valid phone number")
    @Size(min = 12, max = 12, message = "Your phone number must be consist of 10 numbers")
    private String phone;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Please enter characters only.")
    private String city;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "Please enter characters only.")
    private String state;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^\\d{6}$", message = "Please enter a valid zipcode")
    private String zipCode;

    private int completedOrders;

    private boolean isBlocked;

    private int count;

}
