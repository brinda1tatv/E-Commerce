package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShowWalletDto {

    private String currentBalance;
    private String totalExpense;
    private String todayExpense;

}
