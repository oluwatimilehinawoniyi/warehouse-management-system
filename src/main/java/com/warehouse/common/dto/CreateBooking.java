package com.warehouse.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBooking(
        @Schema(description = "Customer Id who owns booking", example = "5b72e9c8-169b-42b0-aa33-f4bb0e6a2ea0")
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @Schema(description = "Storage Id to be booked", example = "5b72e9c8-169b-42b0-aa33-f4bb0e6a2ea0")
        @NotNull(message = "Storage unit ID is required")
        UUID storageUnitId,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @FutureOrPresent(message = "Start date cannot be in the past")
        @NotNull(message = "Start date is required")
        LocalDate endDate,

        @Schema(description = "Rate to charge for storage usage", example = "0.2")
        @DecimalMin(value = "0.01", message = "Rate must be positive")
        @NotNull(message = "Monthly rate is required")
        BigDecimal monthlyRate
) {
}
