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
@Table(name = "ratings")
public class Ratings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "userId")
    private User userId;

    @NotNull
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product productId;

    @NotNull
    private int rating;

    @NotNull
    @NotBlank
    private String heading;

    @NotNull
    @NotBlank
    @Column(name = "review_text")
    private String reviewText;

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
