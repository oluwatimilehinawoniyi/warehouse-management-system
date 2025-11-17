package com.warehouse.common.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBooking(
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotNull(message = "Storage unit ID is required")
        UUID storageUnitId,

        @NotNull(message = "Start date is required")
        LocalDate startDate,

        @FutureOrPresent(message = "Start date cannot be in the past")
        @NotNull(message = "Start date is required")
        LocalDate endDate,

        @DecimalMin(value = "0.01", message = "Rate must be positive")
        @NotNull(message = "Monthly rate is required")
        BigDecimal monthlyRate
) {
}
