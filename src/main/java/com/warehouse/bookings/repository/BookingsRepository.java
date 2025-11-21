package com.warehouse.bookings.repository;

import com.warehouse.bookings.entity.Booking;
import com.warehouse.common.dto.ExpiringBooking;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface BookingsRepository extends JpaRepository<Booking, UUID> {
    /**
     * get bookings that will expire soon for a tenant
     */
    @Query("""
            SELECT new com.warehouse.common.dto.ExpiringBooking(
                b.id,
                c.companyName,
                c.contactEmail,
                b.startDate,
                b.endDate,
                w.name,
                su.unitNumber,
                su.capacityKg,
                b.monthlyRate,
                w.tenantId
            )
            FROM Booking b
            JOIN Customer c ON b.customerId = c.id
            JOIN StorageUnit su ON b.storageUnitId = su.id
            JOIN Warehouse w ON su.warehouseId = w.id
            WHERE w.tenantId = :tenantId
            AND b.endDate <= :endDate
            AND b.status = 'ACTIVE'
            """)
    List<ExpiringBooking> getExpiringBookings(
            @Param("tenantId") UUID tenantId,
            @Param("endDate") LocalDate endDate);

    /**
     * Get ALL expiring bookings across ALL tenants (for scheduler)
     */
    @Query("""
            SELECT new com.warehouse.common.dto.ExpiringBooking(
                b.id,
                c.companyName,
                c.contactEmail,
                b.startDate,
                b.endDate,
                w.name,
                su.unitNumber,
                su.capacityKg,
                b.monthlyRate,
                w.tenantId
            )
            FROM Booking b
            JOIN Customer c ON b.customerId = c.id
            JOIN StorageUnit su ON b.storageUnitId = su.id
            JOIN Warehouse w ON su.warehouseId = w.id
            WHERE b.endDate <= :endDate
            AND b.status = 'ACTIVE'
            """)
    List<ExpiringBooking> getAllExpiringBookings(@Param("endDate") LocalDate endDate);

    /**
     * get booking by tenant id
     *
     * @param tenantId tenant who owns bookings
     */
    @Query("""
            SELECT b from Booking b
            JOIN Customer c ON b.customerId = c.id
            WHERE c.tenantId = :tenantId
            """)
    List<Booking> findByTenantId(
            @Param("tenantId") UUID tenantId);

    /**
     * Get unprocessed expiring bookings ... batch processing needs
     */
    @Query("""
            SELECT new com.warehouse.common.dto.ExpiringBooking(
                b.id,
                c.companyName,
                c.contactEmail,
                b.startDate,
                b.endDate,
                w.name,
                su.unitNumber,
                su.capacityKg,
                b.monthlyRate,
                w.tenantId
            )
            FROM Booking b
                        JOIN Customer c ON b.customerId = c.id
                        JOIN StorageUnit su ON b.storageUnitId = su.id
                        JOIN Warehouse w ON su.warehouseId = w.id
                        WHERE b.endDate <= :endDate
                        AND b.status = 'ACTIVE'
                        AND b.notificationStatus = 'PENDING'
                        ORDER BY b.endDate ASC
            """)
    List<ExpiringBooking> getUnprocessedExpiringBookings (
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    /**
     * Get failed expiring bookings ... for retry processing
     */
    @Query("""
            SELECT new com.warehouse.common.dto.ExpiringBooking(
                b.id,
                c.companyName,
                c.contactEmail,
                b.startDate,
                b.endDate,
                w.name,
                su.unitNumber,
                su.capacityKg,
                b.monthlyRate,
                w.tenantId
            )
            FROM Booking b
            JOIN Customer c ON b.customerId = c.id
            JOIN StorageUnit su ON b.storageUnitId = su.id
            JOIN Warehouse w ON su.warehouseId = w.id
            WHERE b.endDate <= :endDate
            AND b.status = 'ACTIVE'
            AND b.notificationStatus = 'FAILED'
            ORDER BY b.endDate ASC
            """)
    List<ExpiringBooking> getFailedExpiringBookings (
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
