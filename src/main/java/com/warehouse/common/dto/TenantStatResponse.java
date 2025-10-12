package com.warehouse.common.dto;

public record TenantStatResponse(
        long totalWarehouses,
        long totalCustomers,
        long totalBookings,
        double occupancyRate
) {
}
