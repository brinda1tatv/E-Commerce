package com.eCommerce.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
public class Sales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private Orders orderId;

    @NotNull
    @Column(name = "total_amount")
    private double totalAmount;

    @CreationTimestamp
    @Column(name = "added_date_time")
    private LocalDateTime addedDateTime;

}
