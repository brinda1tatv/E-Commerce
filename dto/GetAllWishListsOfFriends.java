package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllWishListsOfFriends {

    private int id;
    private String name;
    private String desc;
    private String ownerName;

}
