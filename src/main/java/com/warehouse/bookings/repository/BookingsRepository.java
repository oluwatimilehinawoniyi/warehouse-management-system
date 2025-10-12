package com.warehouse.bookings.repository;

import com.warehouse.bookings.entity.Booking;
import com.warehouse.common.dto.ExpiringBooking;
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
     * get bookings that will expire soon
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
                b.monthlyRate
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
     * get booking by tenant id
     * @param tenantId tenant who owns bookings
     */
    @Query("""
            SELECT b from Booking b
            JOIN Customer c ON b.customerId = c.id
            WHERE c.tenantId = :tenantId
            """)
    List<Booking> findByTenantId(
            @Param("tenantId") UUID tenantId);
}
