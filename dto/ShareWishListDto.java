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
public class ShareWishListDto {

    @NotBlank(message = "Please enter an email")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "Please enter a valid email address")
    private String email;

    @NotNull
    private int wishId;

    @NotBlank(message = "Please enter description")
    @NotNull(message = "This field must not be null")
    @Size(max = 500, message = "Description must not exceed 500 words")
    private String message;

}
