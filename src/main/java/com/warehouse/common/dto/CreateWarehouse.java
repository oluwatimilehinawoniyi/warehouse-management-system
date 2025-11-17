package com.warehouse.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWarehouse(
        @NotBlank(message = "Warehouse name is required")
        @Size(min = 5, max = 25, message = "Name must be between 5-25 characters")
        String name,

        @NotBlank(message = "Location is required")
        @Size(min = 5, max = 25, message = "Location must be between 5-25 characters")
        String location
) {
}
