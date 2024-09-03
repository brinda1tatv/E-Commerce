package com.eCommerce.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

//@Getter
//@Setter
//@ToString
@Data
@Entity
@Table(
        name = "user",
        uniqueConstraints = @UniqueConstraint(columnNames = {"email"})
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_main_id")
    private UserMain userMainId;

    @NotNull
    @NotBlank
    @Email(message = "Enter your email ")
    private String email;

    @NotNull
    @NotBlank
    private String phone;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role roleId;

    @NotNull
    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @NotBlank
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @NotBlank
    private String gender;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @Column(name = "is_deleted")
    private boolean isDeleted;

//    @Override
//    public String toString() {
//        return "abc";
//    }
}
