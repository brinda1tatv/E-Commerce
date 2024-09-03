package com.eCommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class EditCategoryDto {

    @NotNull(message = "Category Id should not be null.")
    private int catId;

    @NotBlank(message = "Please enter a category name")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Please enter a valid category name")
    private String name;

    @NotBlank(message = "Please enter a Description")
    @Size(max = 1000, message = "Description should not contain more than 1000 words.")
    private String desc;

}
