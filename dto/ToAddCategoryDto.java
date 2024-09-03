package com.eCommerce.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ToAddCategoryDto {

    @NotBlank(message = "Please Enter A Category Name")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Please enter a valid category name")
    private String catName;

    @NotBlank(message = "Please Enter Description")
    @Size(max = 1000, message = "Description should not contain more than 1000 words.")
    private String catDesc;

    @NotNull(message = "Please Enter Number of Sub Categories(Enter zero if not**)")
    private Integer noOfSubCat;

    private List<String> subCats;

}
