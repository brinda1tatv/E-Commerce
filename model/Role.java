package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int role;

    @Column(name = "role_name")
    private String roleName;

}
