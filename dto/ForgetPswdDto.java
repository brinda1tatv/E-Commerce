package com.eCommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ForgetPswdDto {

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "Please enter a valid email address")
    private String email;

}
