package com.eCommerce.dto;

import lombok.Data;

@Data
public class ShowProductDto {

    private int id;
    private String name;
    private String categoryName;
    private String price;
    private String stock;
    private String sellerName;
    private String createdDate;
    private int count;

}
