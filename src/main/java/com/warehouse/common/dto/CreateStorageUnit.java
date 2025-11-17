package com.warehouse.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateStorageUnit(
        @Schema(description = "Storage Unit number", example = "SU-1234")
        @NotBlank(message = "Unit number is required")
        String unitNumber,

        @Schema(description = "Storage Unit capacity in kg", example = "745")
        @Min(value = 1, message = "Capacity must be at least 1 kg")
        @Max(value = 1000, message = "Capacity cannot exceed 1000 kg")
        int capacityKg,

        @Schema(description = "Warehouse Id", example = "5b72e9c8-169b-42b0-aa33-f4bb0e6a2ea0")
        @NotNull(message = "Warehouse ID is required")
        UUID warehouseId
) {
}
