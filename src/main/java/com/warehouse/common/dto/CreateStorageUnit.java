package com.warehouse.common.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateStorageUnit(
        @NotBlank(message = "Unit number is required")
        String unitNumber,

        @Min(value = 1, message = "Capacity must be at least 1 kg")
        @Max(value = 1000, message = "Capacity cannot exceed 1000 kg")
        int capacityKg,

        @NotNull(message = "Warehouse ID is required")
        UUID warehouseId
) {
}
