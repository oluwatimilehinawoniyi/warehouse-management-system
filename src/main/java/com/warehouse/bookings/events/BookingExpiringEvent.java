package com.warehouse.bookings.events;

import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
public class BookingExpiringEvent {
    private final UUID bookingId;
    private final UUID tenantId;
    private final String customerEmail;
    private final String customerCompanyName;
    private final LocalDate expiryDate;
    private final String warehouseName;
    private final String unitNumber;

    public BookingExpiringEvent(
            UUID bookingId,
            UUID tenantId,
            String customerEmail,
            String customerCompanyName,
            LocalDate expiryDate,
            String warehouseName,
            String unitNumber
    ) {
        this.bookingId = bookingId;
        this.tenantId = tenantId;
        this.customerEmail = customerEmail;
        this.customerCompanyName = customerCompanyName;
        this.expiryDate = expiryDate;
        this.warehouseName = warehouseName;
        this.unitNumber = unitNumber;
    }
}
