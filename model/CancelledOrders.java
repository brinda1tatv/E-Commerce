package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "cancelled_orders")
public class CancelledOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "order_id")
    private Orders orderId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @NotNull
    @NotBlank
    private String reason;

    @Column(name = "reason_text")
    private String reasonText;

}
