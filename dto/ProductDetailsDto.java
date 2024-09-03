package com.eCommerce.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductDetailsDto {

    private int productId;
    private List<String> mainImg;
    private String name;
    private String ratings;
    private String cost;
    private String actualCost;
    private String discount;
    private List<String> colors;
    private List<String> colorImgs;
    private String desc;
    private String weight;
    private String createdDate;
    private String seller;
    private String brand;
    private String category;
    private String subCategory;
    private List<String> reviewerName;
    private List<String> reviewStars;
    private List<String> reviewHeadings;
    private List<String> reviewsText;

}
