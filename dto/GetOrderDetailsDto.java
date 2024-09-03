package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetOrderDetailsDto {

    private int id;
    private String product;
    private String qty;
    private String price;
    private int count;

}
