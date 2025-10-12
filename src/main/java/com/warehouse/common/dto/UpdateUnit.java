package com.warehouse.common.dto;

import com.warehouse.storage.entity.StorageStatus;

public record UpdateUnit(
        StorageStatus status,
        Integer capacityKg
) {
}
