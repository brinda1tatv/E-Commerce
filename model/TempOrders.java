package com.eCommerce.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "temp_orders")
public class TempOrders {

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
    private Double discount;

    @NotNull
    @Column(name = "total_items")
    private int totalItems;

    @CreationTimestamp
    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @NotNull
    @Column(name = "is_payment_done")
    private boolean isPaymentDone;

}
