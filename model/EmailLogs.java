package com.eCommerce.model;

import lombok.Data;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "email_log")
public class EmailLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @NotBlank
    @Email(message = "Enter your email ")
    private String email;

    @NotNull
    @Column(name = "sent_date_time")
    private LocalDateTime sentDateTime;

    @NotNull
    @NotBlank
    private String action;

    @NotNull
    @NotBlank
    private String recipient;

}
