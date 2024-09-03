package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddReviewDto {

    @NotNull(message = "This field must not be blank")
    private int pId;

    @NotNull(message = "This field must not be blank")
    private int rating;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String heading;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Size(max = 200, message = "Your Review must no exceed 200 words.")
    private String reviewText;

}
