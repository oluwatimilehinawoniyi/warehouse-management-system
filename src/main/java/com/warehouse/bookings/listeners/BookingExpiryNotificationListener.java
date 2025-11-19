package com.warehouse.bookings.listeners;

import com.warehouse.bookings.events.BookingExpiringEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class BookingExpiryNotificationListener {

    @Async
    @EventListener
    public void handleBookingExpiring(BookingExpiringEvent event) {
        log.info("NOTIFICATION: Booking {} for customer '{}' expires on {}",
                event.getBookingId(), event.getCustomerCompanyName(), event.getExpiryDate());

        log.info("    Would send email to: {}", event.getCustomerEmail());
        log.info("    Message: Your storage unit {} in {} expires in {} day(s)",
                event.getUnitNumber(),
                event.getWarehouseName(),
                event.getExpiryDate().toEpochDay() - LocalDate.now().toEpochDay());

        // email service to send mail wll be provisioned here
    }
}
