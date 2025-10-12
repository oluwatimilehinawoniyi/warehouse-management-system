package com.warehouse.warehouses.repository;

import com.warehouse.common.dto.WarehouseResponse;
import com.warehouse.warehouses.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    List<Warehouse> findByTenantId(UUID tenantId);

    Optional<Warehouse> findByTenantIdAndWarehouseId(UUID tenantId, UUID warehouseId);
}
