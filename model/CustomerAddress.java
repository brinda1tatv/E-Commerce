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
@Table(name = "customer_address")
public class CustomerAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @NotBlank
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @NotBlank
    private String phone;

    @NotNull
    @NotBlank
    private String type;

    @NotNull
    @NotBlank
    private String address;

    @NotNull
    @NotBlank
    private String city;

    @NotNull
    @NotBlank
    private String state;

    @NotNull
    @NotBlank
    @Column(name = "zip_code")
    private String zipCode;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @NotNull
    @Column(name = "is_deleted")
    private boolean isDeleted;

}
