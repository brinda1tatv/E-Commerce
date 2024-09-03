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
@Table (uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    private String code;

    @NotNull
    @NotBlank
    private String type;

    @NotNull
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @NotNull
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @NotNull
    @Column(name = "discount")
    private double discount;

    @NotNull
    @Column(name = "minimum_price")
    private double minimumPrice;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    private User modifiedBy;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @NotNull
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @NotNull
    @Column(name = "applied_count")
    private Integer appliedCount;
}
