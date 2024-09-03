package com.eCommerce.dto;

import lombok.Data;

@Data
public class ShowProductsInCartDto {

    private int id;
    private String name;
    private String cost;
    private String ratings;
    private String image;
    private String discount;
    private String actualCost;
    private String qty;
    private String arrivalDate;
    private String color;
    private int outOfStock;

}
