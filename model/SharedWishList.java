package com.eCommerce.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "shared_wishlist")
public class SharedWishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wishlist_id")
    private WishList wishListId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    private User ownerUserId;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
