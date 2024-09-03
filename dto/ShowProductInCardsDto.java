package com.eCommerce.dto;

import lombok.Data;

@Data
public class ShowProductInCardsDto {

    private int productId;
    private String name;
    private String rating;
    private String cost;
    private String discount;
    private String actualPrice;
    private String imgName;
    private int count;
    private String brand;
    private String sellerName;
    private int wishListId;

}
