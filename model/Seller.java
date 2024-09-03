package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table (
        name = "seller",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email"})
)
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    @Column(name = "business_name")
    private String businessName;

    @NotNull
    @NotBlank
    private String website;

    @NotNull
    @NotBlank
    private String email;

    @NotNull
    @NotBlank
    private String contact;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    private User modifiedBy;

}
