package com.eCommerce.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
        name = "wishlist_tokens",
        uniqueConstraints = @UniqueConstraint(columnNames = {"token"})
)
public class WishListTokens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "wishlist_id")
    private WishList wishListId;

    @NotNull
    @NotBlank
    private String token;

    @NotNull
    @NotBlank
    @Email(message = "Enter your email ")
    @Column(name = "recipient_email")
    private String recipientEmail;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

}
