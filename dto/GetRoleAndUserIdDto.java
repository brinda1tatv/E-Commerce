package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetRoleAndUserIdDto {

    private int roleId;
    private int userId;
    private boolean isBlocked;
    private boolean isDeleted;
    private String firstName;

}
