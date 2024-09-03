package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetDataForOrdersDto {

    private int orderId;
    private String fName;
    private String lName;
    private int productId;
    private String pName;
    private String pCost;
    private String pImage;
    private String address;
    private String city;
    private String state;
    private int zipCode;
    private String paymentMethod;
    private String orderTotal;
    private String orderedDate;
    private String cancelledDate;

}
