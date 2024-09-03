package com.eCommerce.dto;

import lombok.Data;

@Data
public class ToShowCategoryDto {

    private int id;
    private String name;
    private String description;
    private int curPage;
    private int count;

}
