package com.warehouse.common.dto;

import com.warehouse.bookings.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID customerId,
        UUID storageUnitId,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal monthlyRate,
        BookingStatus status,
        LocalDateTime createdAt
) {
}
