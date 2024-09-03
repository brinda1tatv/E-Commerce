package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCouponsDto {

    private int id;
    private String code;
    private String type;
    private String discount;

}
