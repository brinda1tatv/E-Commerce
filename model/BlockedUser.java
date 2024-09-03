package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "blocked_user")
public class BlockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User userId;

    private String reason;

    @Column(name = "isActivated")
    private boolean is_activated;

}
