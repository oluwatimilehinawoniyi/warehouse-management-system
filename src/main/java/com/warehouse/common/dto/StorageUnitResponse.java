package com.warehouse.common.dto;

import com.warehouse.storage.entity.StorageStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record StorageUnitResponse(
        UUID id,
        UUID warehouseId,
        String unitNumber,
        int capacityKg,
        StorageStatus status,
        LocalDateTime createdAt
) {
}
