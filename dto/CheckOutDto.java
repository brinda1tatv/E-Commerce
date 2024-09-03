package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutDto {

    private String firstName;
    private String lastName;
    private String phone;
    private String address;
    private int addressId;
    private String city;
    private String state;
    private String zipCode;
    private String totalItems;
    private String totalDiscount;
    private List<String> items;
    private String totalPrice;

}
