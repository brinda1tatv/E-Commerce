package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllOrdersDto {

    private int id;
    private String orderDate;
    private String total;
    private String orderBy;
    private Boolean isCompleted;
    private String paymentMethod;
    private int count;

}
