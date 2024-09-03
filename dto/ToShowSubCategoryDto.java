package com.eCommerce.dto;

import lombok.Data;

@Data
public class ToShowSubCategoryDto {

    private int id;
    private String catName;
    private String subCatName;
    private String description;
    private int count;

}
