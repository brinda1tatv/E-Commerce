package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "carrier_orders")
public class CarrierOrders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "carrier_id")
    private Carrier carrierId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders orderId;

    @NotNull
    @Column(name = "is_delivered")
    private boolean isDelivered;

    @NotNull
    @Column(name = "not_able_deliver")
    private boolean notAbleToDeliver;

    private String reason;

}
