package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table
public class Carrier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotBlank
    @NotNull
    private String city;

    @NotBlank
    @NotNull
    private String state;

    @NotNull
    @NotBlank
    @Column(name = "zip_code")
    private String zipCode;

    @NotNull
    @Column(name = "completed_orders_count")
    private int completedOrdersCount;

}
