package com.warehouse.warehouses.service;

import com.warehouse.common.dto.CreateWarehouse;
import com.warehouse.common.dto.UpdateWarehouse;
import com.warehouse.common.dto.WarehouseResponse;
import com.warehouse.common.exceptions.NotFoundException;
import com.warehouse.common.mapper.WarehouseMapper;
import com.warehouse.tenants.entity.Tenant;
import com.warehouse.tenants.repository.TenantRepository;
import com.warehouse.warehouses.entity.Warehouse;
import com.warehouse.warehouses.repository.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final TenantRepository tenantRepository;

    @Transactional(readOnly = true)
    public List<WarehouseResponse> getWarehouses(UUID tenantId) {
        return warehouseRepository
                .findByTenantId(tenantId)
                .stream()
                .map(warehouseMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public WarehouseResponse getWarehouse(UUID tenantId, UUID warehouseId) {
        Warehouse w = warehouseRepository
                .findByIdAndTenantId(warehouseId, tenantId)
                .orElseThrow(
                        () -> new NotFoundException("Warehouse not found")
                );
        return warehouseMapper.toDto(w);
    }

    @Transactional
    public WarehouseResponse createWarehouse(UUID tenantId, CreateWarehouse request) {
        Tenant tenant = tenantRepository
                .findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setName(request.name());
        newWarehouse.setLocation(request.location());
        newWarehouse.setTenantId(tenant.getId());
        newWarehouse.setCreatedAt(LocalDateTime.now());
        newWarehouse.setTotalUnits(0);

        Warehouse savedWarehouse = warehouseRepository.save(newWarehouse);
        return warehouseMapper.toDto(savedWarehouse);
    }

    @Transactional
    public WarehouseResponse updateWarehouse(UUID tenantId, UUID warehouseId, UpdateWarehouse request) {
        Warehouse warehouse = warehouseRepository
                .findByIdAndTenantId(warehouseId, tenantId)
                .orElseThrow(() -> new NotFoundException("Warehouse not found"));

        warehouse.setName(request.name());
        warehouse.setLocation(request.location());

        Warehouse updatedWarehouse = warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(updatedWarehouse);
    }

    @Transactional
    public void deleteWarehouse(UUID tenantId, UUID warehouseId) {
        Warehouse warehouse = warehouseRepository
                .findByIdAndTenantId(warehouseId, tenantId)
                .orElseThrow(() -> new NotFoundException("Warehouse not found"));

        warehouseRepository.delete(warehouse);
    }
}
