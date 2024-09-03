package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCouponDto {

    private int id;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^(?=(?:.*[A-Z]){6})(?=(?:.*\\d){2})[A-Z\\d]{8}$", message = "Please enter a valid coupon code")
    private String code;

    @NotNull(message = "This field must not be blank")
    private String startDate;

    @NotNull(message = "This field must not be blank")
    private String endDate;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String type;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[0-9]+$", message = "Please enter a valid minimum amount")
    private String minAmount;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[0-9]+$", message = "Please enter a valid discount")
    private String discount;

    private String startDateTime;

    private String endDateTime;

    private int count;

}
