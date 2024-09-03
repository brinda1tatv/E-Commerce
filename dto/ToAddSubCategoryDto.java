package com.eCommerce.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class ToAddSubCategoryDto {

    @NotBlank(message = "Please Enter A Sub Category Name")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Please enter a valid sub category name")
    private String catName;

    @NotBlank(message = "Please Enter Description")
    @Size(max = 1000, message = "Description should not contain more than 1000 words.")
    private String catDesc;

    @NotNull
    private int catId;

}
