package com.eCommerce.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.descriptor.sql.TinyIntTypeDescriptor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @NotNull
    @NotBlank
    private String color;

    @NotNull
    private int quantity;

    @NotNull
    @Column(name = "is_removed")
    private boolean isRemoved;

    @CreationTimestamp
    @Column(name = "added_date")
    private LocalDateTime addedDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

}
