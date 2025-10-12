package com.warehouse.storage.repository;

import com.warehouse.common.dto.WarehouseUtilization;
import com.warehouse.storage.entity.StorageStatus;
import com.warehouse.storage.entity.StorageUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StorageRepository extends JpaRepository<StorageUnit, UUID> {
    /**
     * find available storage units for a tenant with minimum capacity
     */
    @Query("""
            SELECT su FROM StorageUnit su
            JOIN Warehouse w ON su.warehouseId = w.id
            WHERE w.tenantId = :tenantId
            AND su.capacityKg >= :minCapacityKg
            AND su.status = :storageStatus
            ORDER BY su.capacityKg ASC, w.name ASC
            """)
    List<StorageUnit> findAvailableUnitsByTenantAndCapacity(
            @Param("tenantId") UUID tenantId,
            @Param("minCapacityKg") Integer minCapacityKg,
            @Param("storageStatus") StorageStatus storageStatus);

    /**
     * get warehouse utilization metrics
     */
    @Query("""
            SELECT new com.warehouse.common.dto.WarehouseUtilization(
                w.id,
                w.name,
                w.location,
                COUNT(su.id),
                COUNT(CASE WHEN su.status = 'AVAILABLE' THEN 1 END),
                COUNT(CASE WHEN su.status = 'OCCUPIED' THEN 1 END),
                CAST(COUNT(CASE WHEN su.status = 'OCCUPIED' THEN 1 END) * 100.0 / COUNT(su.id) AS double)
            )
            FROM Warehouse w
            LEFT JOIN StorageUnit su ON w.id = su.warehouseId
            WHERE w.tenantId = :tenantId
            GROUP BY w.id, w.name, w.location
            ORDER BY w.name
            """)
    List<WarehouseUtilization> getWarehouseUtilizationByTenant(@Param("tenantId") UUID tenantId);

    /**
     * get a particular warehouse utilization metrics
     */
    @Query("""
            SELECT new com.warehouse.common.dto.WarehouseUtilization(
                w.id,
                w.name,
                w.location,
                COUNT(su.id),
                COUNT(CASE WHEN su.status = 'AVAILABLE' THEN 1 END),
                COUNT(CASE WHEN su.status = 'OCCUPIED' THEN 1 END),
                CAST(COUNT(CASE WHEN su.status = 'OCCUPIED' THEN 1 END) * 100.0 / COUNT(su.id) AS double)
            )
            FROM Warehouse w
            LEFT JOIN StorageUnit su ON w.id = su.warehouseId
            WHERE w.id = :warehouseId
            AND w.tenantId = :tenantId
            """)
    WarehouseUtilization getSingleWarehouseUtilizationByTenant(
            @Param("warehouseId") UUID warehouseId,
            @Param("tenantId") UUID tenantId
    );

    /**
     * find units by warehouse
     */
    List<StorageUnit> findByWarehouseIdAndStatus(UUID warehouseId, StorageStatus status);

    List<StorageUnit> findByWarehouseIdAndWarehouse_TenantId(UUID warehouseId, UUID tenantId);

    List<StorageUnit> findByWarehouse_TenantId(UUID tenantId);

    @Query("""
            SELECT su FROM StorageUnit su
            JOIN Warehouse w ON su.warehouseId = w.id
            WHERE su.id = :unitId AND w.tenantId = :tenantId
            """)
    Optional<StorageUnit> findUnitByTenantId(@Param("unitId") UUID unitId,
                                            @Param("tenantId") UUID tenantId);

}
