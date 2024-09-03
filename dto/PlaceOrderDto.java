package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderDto {

    @NotNull(message = "This field must not be null")
    private Integer addrId;

    @NotNull(message = "This field must not be null")
    private Integer method;

    @NotNull(message = "This field must not be null")
    private Double total;

    @NotNull(message = "This field must not be null")
    private Double totalSum;

    private Integer couponId;

}
