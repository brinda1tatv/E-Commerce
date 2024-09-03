package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetNotificationsDto {

    private int id;
    private String subject;
    private String description;
    private boolean isSeen;
    private String dateTime;

}
