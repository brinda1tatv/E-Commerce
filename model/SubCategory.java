package com.eCommerce.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(
        name = "sub_category",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name"})
)
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category categoryId;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

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
