package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUsersDto {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String contact;
    private int role;
    private String roleName;
    private boolean isBlocked;
    private String createdDate;
    private int count;

}
