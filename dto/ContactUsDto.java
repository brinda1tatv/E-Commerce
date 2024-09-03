package com.eCommerce.dto;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class ContactUsDto {

        @NotBlank(message = "Please Enter Your Name")
        @NotNull(message = "Please Enter Your Name")
        private String name;

        @NotBlank(message = "Please Enter Your Email")
        @NotNull(message = "Please Enter Your Email")
        @Email(message = "Please enter a valid email address")
        @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "Please enter a valid email address")
        private String email;

        @NotBlank(message = "Please Enter Your Phone Number")
        @NotNull(message = "Please Enter Your Phone Number")
        @Pattern(regexp = "^((91[0-9]{10}))$", message = "Please enter a valid phone number")
        @Size(min = 12, max = 12, message = "Your phone number must be consist of 10 numbers")
        private String phone;

        @NotBlank(message = "Please Enter A Message")
        @NotNull(message = "Please Enter A Message")
        @Size(max = 1000, message = "Message could not contain more than 1000 words")
        private String message;

}


