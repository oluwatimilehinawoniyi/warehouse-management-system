package com.warehouse.common.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ExpiringBooking(
        UUID bookingId,
        String customerCompanyName,
        String customerEmail,
        LocalDate startDate,
        LocalDate endDate,
        String warehouseName,
        String unitNumber,
        Integer capacityKg,
        BigDecimal monthlyRate
) {}
