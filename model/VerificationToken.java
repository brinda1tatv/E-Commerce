package com.eCommerce.model;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    private String token;

    @NotNull
    @NotBlank
    @Email(message = "Enter your email ")
    private String email;

    @NotNull
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    private boolean validation;

}
