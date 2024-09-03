package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllCarrierOrders {

    private int carrierId;
    private int orderId;
    private int carrierOrderId;
    private String totalPrice;
    private String customer;
    private String city;
    private String state;
    private String zipCode;
    private boolean isCompleted;
    private boolean canNotDeliver;
    private int count;

}
