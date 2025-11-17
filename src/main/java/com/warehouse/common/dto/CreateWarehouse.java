package com.warehouse.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWarehouse(
        @Schema(description = "Display name of the warehouse", example = "Lagos Main Warehouse")
        @NotBlank(message = "Warehouse name is required")
        @Size(min = 3, max = 25, message = "Name must be between 3-25 characters")
        String name,

        @Schema(description = "Physical address or city", example = "Ikeja, Lagos")
        @NotBlank(message = "Location is required")
        @Size(min = 3, max = 25, message = "Location must be between 3-25 characters")
        String location
) {
}
