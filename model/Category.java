package com.eCommerce.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
        name = "category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name"})
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String description;

    @NotNull
    @Column(name = "total_sub_categories")
    private int totalSubCategories;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @NotNull
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

}
