package com.warehouse.common.dto;

import java.util.UUID;

public record CreateStorageUnit(
        String unitNumber,
        int capacityKg,
        UUID warehouseId
) {
}
