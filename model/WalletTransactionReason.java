package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "wallet_transaction_reason")
public class WalletTransactionReason {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    private String reason;

    @NotNull
    @NotBlank
    private String type;

    @NotNull
    @Column(name = "min_price")
    private Double minPrice;

    @NotNull
    @Column(name = "max_price")
    private Double maxPrice;

    @NotNull
    private Double percentage;

}
