package com.warehouse.tenants.repository;

import com.warehouse.common.dto.TenantStatResponse;
import com.warehouse.tenants.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByEmail(String email);

    @Query("""
            SELECT new com.warehouse.common.dto.TenantStatResponse(
            COUNT(DISTINCT w.id),
            COUNT(DISTINCT c.id),
            COUNT(DISTINCT b.id),
            (CASE WHEN COUNT(su.id) = 0
                  THEN 0
                  ELSE (COUNT(CASE WHEN su.status = 'OCCUPIED' THEN 1 END) * 100.0 / COUNT(su.id))
             END)
            FROM Tenant t
            LEFT JOIN Warehouse w ON w.tenantId = t.id
            LEFT JOIN StorageUnit su ON w.id = su.warehouseId
            LEFT JOIN Customer c ON c.tenantId = t.id
            LEFT JOIN Booking b ON b.customer_id = c.id
            WHERE t.id = :tenantId
            """)
    TenantStatResponse getStatisticsForTenant(@Param("tenantId") UUID tenantId);
}
