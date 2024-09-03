package com.eCommerce.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "contact_us")
public class ContactUs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    @NotNull
    private String name;

    @NotBlank
    @NotNull
    private String email;

    @NotBlank
    @NotNull
    private String phone;

    @NotBlank
    @NotNull
    private String message;

}
