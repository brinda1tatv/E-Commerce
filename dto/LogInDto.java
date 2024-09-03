package com.eCommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LogInDto {

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).{6,}$", message = "Please enter a valid Password")
    @Size(max = 6, message = "Password must not contain more than 6 letters")
    @Size(min = 6, message = "Password must contain atleast 6 letters")
    private String pswd;

}
