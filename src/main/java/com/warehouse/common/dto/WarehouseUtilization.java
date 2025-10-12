package com.warehouse.common.dto;

import java.util.UUID;

public record WarehouseUtilization(
        UUID warehouseId,
        String warehouseName,
        String location,
        long totalUnits,
        long availableUnits,
        long occupiedUnits,
        double occupancyRate
) {
}
