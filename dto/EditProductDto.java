package com.eCommerce.dto;

import lombok.Data;

@Data
public class EditProductDto {

    private String name;

    private int catId;

    private int subCatId;

    private String brand;

    private Double weight;

    private String color;

    private Double cost;

    private Double actualCost;

    private Integer stock;

    private Integer discount;

    private String prodDesc;

    private String tags;

    private String busiName;

    private String busiEmail;

    private String busiWebsite;

    private String busiPhone;

    private String formFileMultiple;

    private String formFile;

}
