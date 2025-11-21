package com.warehouse.bookings.service;

import com.warehouse.bookings.entity.Booking;
import com.warehouse.bookings.entity.NotificationStatus;
import com.warehouse.bookings.events.BookingExpiringEvent;
import com.warehouse.bookings.repository.BookingsRepository;
import com.warehouse.common.dto.ExpiringBooking;
import com.warehouse.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingExpiryScheduler {
    private final BookingsRepository bookingsRepository;
    private final ApplicationEventPublisher eventPublisher;

    private static final int BATCH_SIZE = 20;
    private static final int MAX_RETRIES = 3;

    @Scheduled(cron = "0 0 9 * * *") // run daily at 9am
    @Transactional
    public void checkExpiringBookings() {
        log.info("Starting daily expiring bookings check...");

        LocalDate sevenDaysFromNow = LocalDate
                .now().plusDays(7);
        int processedCount = 0;

        while (true) {
            Pageable pageable = PageRequest.of(0, BATCH_SIZE);
            List<ExpiringBooking> batch = bookingsRepository.getUnprocessedExpiringBookings(sevenDaysFromNow, pageable);

            if (batch.isEmpty()) {
                break;
            }

            for (ExpiringBooking booking : batch) {
                try {
                    publishEvent(booking);
                    markBookingAsProcessed(booking.bookingId());
                    processedCount++;
                } catch (Exception e) {
                    log.error("Failed to process booking {}: {}",
                            booking.bookingId(), e.getMessage());
                    markBookingAsFailed(booking.bookingId());
                }
            }

            log.info("Processed batch of {} bookings", batch.size());
        }
        log.info("Completed daily check. Published {} expiring booking events", processedCount);
    }

    @Scheduled(fixedRate = 3600000) // hourly runs
    @Transactional
    public void retryFailedNotifications() {
        log.info("Checking for failed notifications to retry...");

        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        Pageable pageable = PageRequest.of(0, BATCH_SIZE);
        List<ExpiringBooking> failedBatch = bookingsRepository
                .getFailedExpiringBookings(sevenDaysFromNow, pageable);

        if (failedBatch.isEmpty()) {
            log.info("No failed notifications to retry");
            return;
        }

        int retriedCount = 0;
        for (ExpiringBooking booking : failedBatch) {
            try {
                publishEvent(booking);
                markBookingAsProcessed(booking.bookingId());
                log.info("Successfully retired booking {}", booking.bookingId());
                retriedCount++;
            } catch (Exception e) {
                log.error("Retry failed for booking {}: {}",
                        booking.bookingId(), e.getMessage());
                incrementRetryCount(booking.bookingId());
            }
        }
        log.info("Retry completed. Successfully retried {} notifications", retriedCount);
    }

    private void publishEvent(ExpiringBooking booking) {
        BookingExpiringEvent event = new BookingExpiringEvent(
                booking.bookingId(),
                booking.tenantId(),
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

    private void markBookingAsProcessed(UUID bookingId) {
        Booking booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        booking.setNotificationStatus(NotificationStatus.PROCESSED);
        bookingsRepository.save(booking);
    }

    private void markBookingAsFailed(UUID bookingId) {
        Booking booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        booking.setNotificationStatus(NotificationStatus.FAILED);
        bookingsRepository.save(booking);
    }

    private void incrementRetryCount(UUID bookingId) {
        Booking booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));

        int currentRetries = booking.getRetryCount() != null ? booking.getRetryCount() : 0;
        booking.setRetryCount(currentRetries + 1);

        if (booking.getRetryCount() >= MAX_RETRIES) {
            booking.setNotificationStatus(NotificationStatus.ABANDONED);
            log.error("Booking {} abandoned after {} retry attempts", bookingId, MAX_RETRIES);
        } else {
            booking.setNotificationStatus(NotificationStatus.FAILED);
        }

        bookingsRepository.save(booking);
    }
}
