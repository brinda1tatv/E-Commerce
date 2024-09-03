package com.eCommerce.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
