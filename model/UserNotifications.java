package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(
        name = "user_notifications",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"})
)
public class UserNotifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    @Column(name = "is_noti_on")
    private boolean isNotiOn;

}
