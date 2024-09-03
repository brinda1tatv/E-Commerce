package com.eCommerce.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "seen_notifications")
public class SeenNotifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "notifications_id")
    private Notifications notificationsId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

}
