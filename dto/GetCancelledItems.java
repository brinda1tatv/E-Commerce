package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCancelledItems {

    private int id;
    private int orderId;
    private String product;
    private String cancelledBy;
    private String reason;
    private String notes;
    private int count;

}
