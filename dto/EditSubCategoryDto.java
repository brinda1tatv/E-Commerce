package com.eCommerce.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class EditSubCategoryDto {

    @NotNull(message = "Sub Category Id should not be null.")
    private int subcateId;

    @NotNull(message = "Category Id should not be null.")
    private int catId;

    @NotBlank(message = "Please Enter a Sub category name")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Please enter a valid sub category name")
    private String name;

    @NotBlank(message = "Please Enter a Description")
    @Size(max = 1000, message = "Description should not contain more than 1000 words.")
    private String desc;

}
