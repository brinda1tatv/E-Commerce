package com.eCommerce.helper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateHelper {

    public String getDateInFormate(LocalDate date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");

        String formattedDate = date.format(formatter);

        return formattedDate;

    }

}
