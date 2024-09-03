package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "order_details")
public class OrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orderId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @NotNull
    private int quantity;

    @NotNull
    @NotBlank
    private String color;

    @NotNull
    private Double price;

    @NotNull
    @Column(name = "is_cancelled")
    private boolean isCancelled;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

}
