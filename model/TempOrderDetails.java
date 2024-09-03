package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "temp_order_details")
public class TempOrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "order_id")
    private TempOrders orderId;

    @NotNull
    @NotBlank
    @Column(name = "product_id")
    private String productId;

    @NotNull
    @NotBlank
    private String quantity;

    @NotNull
    @NotBlank
    private String colors;

}
