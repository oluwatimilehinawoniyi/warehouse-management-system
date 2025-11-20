package com.warehouse.bookings.service;

import com.warehouse.bookings.events.BookingExpiringEvent;
import com.warehouse.bookings.repository.BookingsRepository;
import com.warehouse.common.dto.ExpiringBooking;
import com.warehouse.tenants.entity.Tenant;
import com.warehouse.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingExpiryScheduler {
    private final BookingsRepository bookingsRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 9 * * *") // run daily at 9am
    public void checkExpiringBookings() {
        log.info("Starting daily expiring bookings check...");

        LocalDate sevenDaysFromNow = LocalDate
                .now().plusDays(7);

        List<ExpiringBooking> allExpiringBookings = bookingsRepository
                .getAllExpiringBookings(sevenDaysFromNow);

        log.info("Found {} expiring bookings across all tenants", allExpiringBookings.size());

        for (ExpiringBooking booking : allExpiringBookings) {
            publishEvent(booking, booking.tenantId());
        }

        log.info("Published {} expiring booking events", allExpiringBookings.size());
    }

    private void publishEvent(ExpiringBooking booking, UUID tenantId) {
        BookingExpiringEvent event = new BookingExpiringEvent(
                booking.bookingId(),
                tenantId,
                booking.customerEmail(),
                booking.customerCompanyName(),
                booking.endDate(),
                booking.warehouseName(),
                booking.unitNumber()
        );

        eventPublisher.publishEvent(event);
        log.debug("Published event for booking {} expiring on {}",
                booking.bookingId(), booking.endDate());
    }
}
