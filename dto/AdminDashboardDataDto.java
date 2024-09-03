package com.eCommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardDataDto {

    private long totalOrders;
    private long totalUsers;
    private Double totalSales;
    private Double totalRevenue;
    private List<Double> monthlySales;
    private List<Double> monthlyRevenue;
    private long totalOrderedItems;
    private long cancelledItems;
    private int rateOfCancellation;

}
