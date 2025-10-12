package com.warehouse.bookings.service;

import com.warehouse.bookings.entity.Booking;
import com.warehouse.bookings.entity.BookingStatus;
import com.warehouse.bookings.repository.BookingsRepository;
import com.warehouse.common.dto.CreateBooking;
import com.warehouse.common.dto.ExpiringBooking;
import com.warehouse.common.dto.UpdateBooking;
import com.warehouse.common.exceptions.NotFoundException;
import com.warehouse.common.exceptions.UnauthorizedAccessException;
import com.warehouse.common.mapper.BookingMapper;
import com.warehouse.customers.entity.Customer;
import com.warehouse.customers.repository.CustomersRepository;
import com.warehouse.storage.entity.StorageUnit;
import com.warehouse.storage.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingsRepository bookingsRepository;
    private final BookingMapper bookingMapper;
    private final CustomersRepository customersRepository;
    private final StorageRepository storageRepository;

    /**
     * Get bookings that are expiring in a given date
     *
     * @param tenantId owner of warehouse/storage
     * @param endDate  given date booking ends
     */
    @Transactional(readOnly = true)
    public List<ExpiringBooking> getExpiringBookings(
            UUID tenantId,
            LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("Select a desired end date period");
        }

        if (endDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("End date cannot be in the past");
        }

        return bookingsRepository.getExpiringBookings(tenantId, endDate);
    }

    /**
     * Get bookings
     *
     * @param tenantId owner of warehouse/storage
     */
    @Transactional(readOnly = true)
    public Object getBookings(UUID tenantId) {
        List<Booking> bookings = bookingsRepository
                .findByTenantId(tenantId);

        return bookings
                .stream()
                .map(bookingMapper::toDto)
                .toList();
    }

    /**
     * Get booking
     *
     * @param tenantId  owner of warehouse/storage
     * @param bookingId id of the booking to get
     */
    @Transactional(readOnly = true)
    public Object getBooking(UUID bookingId, UUID tenantId) {
        Booking booking = validateBookingOwnership(bookingId, tenantId);
        return bookingMapper.toDto(booking);
    }

    /**
     * Create a new booking
     *
     * @param request information to create a new booking
     * @return a booking dto
     */
    @Transactional
    public Object createBooking(CreateBooking request) {
        Booking booking = new Booking();

        StorageUnit storageUnit = storageRepository.findById(request.storageUnitId())
                .orElseThrow(() -> new NotFoundException("Storage Unit not found"));

        Customer customer = customersRepository.findById(request.customerId())
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        booking.setCustomerId(request.customerId());
        booking.setStorageUnitId(request.storageUnitId());
        booking.setStorageUnit(storageUnit);
        booking.setStartDate(request.startDate());
        booking.setEndDate(request.endDate());
        booking.setMonthlyRate(request.monthlyRate());
        booking.setStatus(BookingStatus.ACTIVE);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setCustomer(customer);

        Booking newBooking = bookingsRepository.save(booking);

        return bookingMapper.toDto(newBooking);
    }

    /**
     * Update a booking
     *
     * @param tenantId  owner of warehouse/storage
     * @param bookingId id of the booking to update
     * @param request   a record of the update request
     * @return updated booking
     */
    @Transactional
    public Object updateBooking(
            UUID bookingId,
            UUID tenantId,
            UpdateBooking request) {
        Booking booking = validateBookingOwnership(bookingId, tenantId);

        if (request.status() != null) {
            booking.setStatus(request.status());
        }
        if (request.endDate() != null) {
            booking.setEndDate(request.endDate());
        }
        if (request.monthlyRate() != null) {
            booking.setMonthlyRate(request.monthlyRate());
        }

        Booking updatedBooking = bookingsRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);
    }

    /**
     * Delete a booking
     *
     * @param bookingId booking to be deleted
     * @param tenantId  tenant
     */
    @Transactional
    public void deleteBooking(UUID bookingId, UUID tenantId) {
        Booking booking = validateBookingOwnership(bookingId, tenantId);
        bookingsRepository.delete(booking);
    }

    private Booking validateBookingOwnership(UUID bookingId, UUID tenantId) {
        Booking booking = bookingsRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        Customer customer = customersRepository.findById(booking.getCustomerId())
                .orElseThrow();

        if (!customer.getTenantId().equals(tenantId)) {
            throw new UnauthorizedAccessException("Unauthorized Access");
        }
        return booking;
    }
}
