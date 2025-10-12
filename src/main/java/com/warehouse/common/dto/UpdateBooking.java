package com.warehouse.common.dto;

import com.warehouse.bookings.entity.BookingStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateBooking(
        LocalDate endDate,
        BigDecimal monthlyRate,
        BookingStatus status
) {
}
