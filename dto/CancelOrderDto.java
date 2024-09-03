package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelOrderDto {

    @NotNull(message = "This field must not be null")
    private int prodId;

    @NotNull(message = "This field must not be null")
    private int ordId;

    @NotBlank(message = "The cancellation type field must not be blank")
    @NotNull(message = "The cancellation type field must not be null")
    private String cancellationType;

    @NotBlank(message = "The cancellation reason field must not be blank")
    @NotNull(message = "The cancellation reason field must not be null")
    private String cancellationReason;

    private String reason;

}
