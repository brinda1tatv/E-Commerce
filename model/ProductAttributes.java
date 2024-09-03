package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "product_attributes")
public class ProductAttributes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @NotNull
    @NotBlank
    private String color;

    @Column(name = "actual_cost")
    private Double actualCost;

    private Integer discount;

}
