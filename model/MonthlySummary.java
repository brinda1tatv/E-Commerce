package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "monthly_summary")
public class MonthlySummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    @NotBlank
    @Column(name = "month_year")
    private String monthYear;

    @NotNull
    @Column(name = "total_purchase")
    private Double totalPurchase;

}
