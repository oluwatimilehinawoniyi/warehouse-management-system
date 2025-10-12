package com.warehouse.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateBooking(
        UUID customerId,
        UUID storageUnitId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal monthlyRate
) {
}
