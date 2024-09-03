package com.eCommerce.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table (
        name = "product",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name"})
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categoryId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "subCategory_id")
    private SubCategory subCategoryId;

    @NotNull
    @NotBlank
    private String brand;

    @NotNull
    private Double weight;

    @NotNull
    private Double cost;

    @NotNull
    @NotBlank
    @Column(name = "prod_description")
    private String prodDescription;

    @NotNull
    @NotBlank
    private String tags;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    private User modifiedBy;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller sellerId;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @NotNull
    @OneToOne(mappedBy = "productId", cascade = CascadeType.ALL)
    private ProductAttributes productAttributes;

}
