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
public class Notifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    @NotBlank
    private String subject;

    @NotNull
    @NotBlank
    private String description;

    @NotNull
    private int type;

    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

}
