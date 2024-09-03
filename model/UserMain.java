package com.eCommerce.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

//@Getter
//@Setter
//@ToString
@Data
@Entity
@Table(
        name = "user_main",
        uniqueConstraints = @UniqueConstraint(columnNames = {"password_hash"})
)
public class UserMain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    @Column(name = "password_hash")
    private String passwordHash;

    @NotNull
    @NotBlank
    private String salt;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @UpdateTimestamp
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @ManyToOne
    @JoinColumn(name = "modified_by")
    private User modifiedBy;

//    @Override
//    public String toString() {
//        return "xyz";
//    }

}
