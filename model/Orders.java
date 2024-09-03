package com.eCommerce.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    @Column(name = "total_amount")
    private Double totalAmount;

    @NotNull
    @NotBlank
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @CreationTimestamp
    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @NotNull
    @Column(name = "estimated_date")
    private LocalDateTime estimatedDate;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @NotNull
    @Column(name = "is_cancelled")
    private boolean isCancelled;

    @NotNull
    @Column(name = "is_single_item_cancelled")
    private boolean isSingleItemCancelled;

    @NotNull
    @Column(name = "is_completed")
    private boolean isCompleted;

    @NotNull
    @Column(name = "is_assigned")
    private boolean isAssigned;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "customer_address_id")
    private CustomerAddress customerAddressId;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon couponId;

    @Column(name = "is_wallet_used")
    private boolean isWalletUsed;

}
