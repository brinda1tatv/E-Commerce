package com.eCommerce.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    private Double balance;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wallet_transaction_reason_id")
    private WalletTransactionReason walletTransactionReasonId;

    @CreationTimestamp
    @Column(name = "transaction_date")
    private LocalDateTime transactionaDate;

    @NotNull
    @Column(name = "transaction_amount")
    private Double transactionAmount;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "transaction_type", length = 6)
    private TransactionType transactionType;

    public enum TransactionType {
        CREDIT, DEBIT
    }

}
