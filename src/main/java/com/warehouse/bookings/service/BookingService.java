package com.warehouse.bookings.service;

import com.warehouse.bookings.entity.Booking;
import com.warehouse.bookings.entity.BookingStatus;
import com.warehouse.bookings.repository.BookingsRepository;
import com.warehouse.common.dto.CreateBooking;
import com.warehouse.common.dto.ExpiringBooking;
import com.warehouse.common.dto.UpdateBooking;
import com.warehouse.common.exceptions.BookingConflictException;
import com.warehouse.common.exceptions.NotFoundException;
import com.warehouse.common.exceptions.UnauthorizedAccessException;
import com.warehouse.common.mapper.BookingMapper;
import com.warehouse.customers.entity.Customer;
import com.warehouse.customers.repository.CustomersRepository;
import com.warehouse.storage.entity.StorageStatus;
import com.warehouse.storage.entity.StorageUnit;
import com.warehouse.storage.repository.StorageRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
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
     * @param tenantId tenantId
     * @param request  information to create a new booking
     * @return a booking dto
     * @throws BookingConflictException if the unit was just booked by another customer
     */
    @CacheEvict(value = "tenantStats", key = "#tenantId")
    public Object createBooking(UUID tenantId, CreateBooking request) {
        try {
            StorageUnit storageUnit = storageRepository
                    .findById(request.storageUnitId())
                    .orElseThrow(() -> new NotFoundException("Storage Unit not found"));

            if (storageUnit.getStatus() != StorageStatus.AVAILABLE) {
                throw new IllegalStateException(
                        "Storage unit is not available. Current status: " + storageUnit.getStatus()
                );
            }

            Booking booking = new Booking();

            Customer customer = customersRepository.findById(request.customerId())
                    .orElseThrow(() -> new NotFoundException("Customer not found"));

            if (!customer.getTenantId().equals(tenantId)) {
                throw new UnauthorizedAccessException(
                        "Customer does not belong to the same tenant as the storage unit"
                );
            }

            booking.setCustomerId(request.customerId());
            booking.setStorageUnitId(request.storageUnitId());
            booking.setStartDate(request.startDate());
            booking.setEndDate(request.endDate());
            booking.setMonthlyRate(request.monthlyRate());
            booking.setStatus(BookingStatus.ACTIVE);
            booking.setCreatedAt(LocalDateTime.now());
            booking.setStorageUnit(storageUnit);
            booking.setCustomer(customer);

            storageUnit.setStatus(StorageStatus.OCCUPIED);
            storageRepository.save(storageUnit);

            Booking newBooking = bookingsRepository.save(booking);
            return bookingMapper.toDto(newBooking);
        } catch (OptimisticLockException | ObjectOptimisticLockingFailureException e) {
            log.warn("Optimistic lock failure when booking unit {}: {}",
                    request.storageUnitId(), e.getMessage());
            throw new BookingConflictException(
                    "This storage unit was just booked by another customer. Please select another unit."
            );
        }

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
