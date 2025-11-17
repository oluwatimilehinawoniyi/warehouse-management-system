package com.warehouse.bookings.controller;

import com.warehouse.bookings.service.BookingService;
import com.warehouse.common.dto.CreateBooking;
import com.warehouse.common.dto.UpdateBooking;
import com.warehouse.common.response.ResponseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookings")
public class BookingsController {
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestParam UUID tenantId) {
        return ResponseHandler.responseBuilder(
                "Bookings successfully returned",
                HttpStatus.OK,
                bookingService.getBookings(tenantId)
        );
    }

    @GetMapping("/expiring?endDate={endDate}")
    public ResponseEntity<Object> getExpiringBookings(
            @RequestParam UUID tenantId,
            @RequestParam LocalDate endDate) {
        return ResponseHandler.responseBuilder(
                "Expiring bookings successfully returned",
                HttpStatus.OK,
                bookingService.getExpiringBookings(tenantId, endDate)
        );
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@PathVariable UUID bookingId, @RequestParam UUID tenantId) {
        return ResponseHandler.responseBuilder(
                "Booking successfully returned",
                HttpStatus.OK,
                bookingService.getBooking(bookingId, tenantId)
        );
    }

    @PostMapping("/")
    public ResponseEntity<Object> createBooking(
            @RequestParam UUID tenantId,
            @Valid @RequestBody CreateBooking request) {
        return ResponseHandler.responseBuilder(
                "Booking successfully created",
                HttpStatus.CREATED,
                bookingService.createBooking(tenantId, request)
        );
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(
            @PathVariable UUID bookingId,
            @RequestParam UUID tenantId,
            @RequestBody UpdateBooking request) {
        return ResponseHandler.responseBuilder(
                "Booking successfully updated",
                HttpStatus.OK,
                bookingService.updateBooking(bookingId, tenantId, request)
        );
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Object> deleteBooking(
            @RequestParam UUID tenantId,
            @PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId, tenantId);
        return ResponseHandler.responseBuilder(
                "Booking successfully deleted",
                HttpStatus.NO_CONTENT,
                Map.of("success", true)
        );
    }
}
