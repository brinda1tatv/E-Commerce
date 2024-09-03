package com.eCommerce.model;

import lombok.Data;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(
        name = "user_sessions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "session_id"})
)
public class UserSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @NotNull
    @NotBlank
    @Column(name = "session_id")
    private String sessionId;

}
