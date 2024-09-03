package com.eCommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AddNewWishListDto {

    @NotBlank(message = "Please enter a name")
    @NotNull(message = "This field must not be null")
    private String wishlistName;

    @NotBlank(message = "Please enter description")
    @NotNull(message = "This field must not be null")
    @Size(max = 500, message = "Description must not exceed 500 words")
    private String wishlistDescription;

}
