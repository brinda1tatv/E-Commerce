package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProceedToBuyDto {

    @NotNull(message = "This field must not be null")
    private List<Integer> products;

    @NotNull(message = "This field must not be null")
    private int totalItems;

    @NotNull(message = "This field must not be null")
    private double totalPrice;

    @NotNull(message = "This field must not be null")
    private double totalDiscount;

    @NotNull(message = "This field must not be null")
    private List<String> colors;

}
