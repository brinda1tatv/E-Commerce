package com.eCommerce.dto;

import lombok.Data;
import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class AddProductDto {

    private int pId;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Please enter a valid product name")
    private String name;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String catName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    private String subCatName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Please enter a valid brand name")
    private String brand;

    @NotNull(message = "This field must not be null")
    private Double weight;

    @NotNull(message = "This field must not be null")
    private List<String> color;

    @NotNull(message = "This field must not be null")
    private Double cost;

    @NumberFormat
    private Double actualCost;

    @NotNull(message = "This field must not be null")
    private Integer stock;

    @NumberFormat
    private Integer discount;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Size(max = 500, message = "Description must not exceed 500 words")
    private String prodDesc;

    @NotNull(message = "This field must not be null")
    private List<String> tags;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Please enter a valid business name")
    private String bName;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$", message = "Please enter a valid email address")
    private String bEmail;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$", message = "Please enter a valid website")
    private String bWebsite;

    @NotBlank(message = "This field must not be blank")
    @NotNull(message = "This field must not be null")
    @Pattern(regexp = "^((91[0-9]{10}))$", message = "Please enter a valid phone number")
    @Size(min = 12, max = 12, message = "Your phone number must be consist of 10 numbers")
    private String bPhone;

    private List<MultipartFile> formFileMultiple;

    private MultipartFile formFile;

}
