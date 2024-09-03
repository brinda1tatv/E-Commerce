package com.eCommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class OtpValidationDto {

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z]+(?:\\s[a-zA-Z]+)$", message = "Please enter fisrt name and last name. Special Characters are not allowed.")
    private String name;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^((91[0-9]{10}))$", message = "Please enter a valid phone number")
    @Size(min = 12, max = 12, message = "Your phone number must be consist of 10 numbers")
    private String phone;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String gender;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{6,}$", message = "Please enter a valid Password")
    @Size(max = 6, message = "Password must not contain more than 6 letters")
    @Size(min = 6, message = "Password must contain atleast 6 letters")
    private String pswd;

    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^\\d+$", message = "Please enter numbers only")
    @Size(max = 6, message = "OTP should not have more than 6 letters")
    @Size(min = 6, message = "OTP must contain 6 letters")
    private String otp;

}
