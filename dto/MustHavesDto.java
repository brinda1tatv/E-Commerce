package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MustHavesDto {

    private int id;
    private String productName;
    private String image;
    private String brand;

}
