package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowPaymentHistoryDto {

    private String date;
    private String desc;
    private String amount;
    private String type;
    private int count;

}
