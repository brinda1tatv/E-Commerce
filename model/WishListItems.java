package com.eCommerce.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "wishlist_items")
public class WishListItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wishlist_id")
    private WishList wishListId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @NotNull
    @Column(name = "is_removed")
    private boolean isRemoved;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
