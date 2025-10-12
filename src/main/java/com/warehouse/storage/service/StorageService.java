package com.warehouse.storage.service;

import com.warehouse.common.dto.CreateStorageUnit;
import com.warehouse.common.dto.StorageUnitResponse;
import com.warehouse.common.dto.UpdateUnit;
import com.warehouse.common.dto.WarehouseUtilization;
import com.warehouse.common.exceptions.CapacityReductionNotAllowedException;
import com.warehouse.common.exceptions.InvalidCapacityException;
import com.warehouse.common.exceptions.NotFoundException;
import com.warehouse.common.mapper.StorageMapper;
import com.warehouse.storage.entity.StorageStatus;
import com.warehouse.storage.entity.StorageUnit;
import com.warehouse.storage.repository.StorageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageService {
    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    /**
     * Get available storage units for a tenant, with a minimum capacity
     *
     * @param tenantId      tenant id
     * @param minCapacityKg minimum capacity of storages to get
     * @return a list of available storage units (storages with status as available)
     */
    @Transactional(readOnly = true)
    public List<StorageUnit> getAvailableUnits(
            UUID tenantId,
            Integer minCapacityKg
    ) {
        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID is required");
        }

        if (minCapacityKg == null || minCapacityKg < 0) {
            minCapacityKg = 0;
        }

        return storageRepository.findAvailableUnitsByTenantAndCapacity(
                tenantId,
                minCapacityKg,
                StorageStatus.AVAILABLE);
    }

    /**
     * Get warehouse utilization summary (e.g. where one wants to see how full each warehouse is)
     *
     * @param tenantId tenant id
     */
    public List<WarehouseUtilization> getWarehouseUtilization(UUID tenantId) {
        return storageRepository.getWarehouseUtilizationByTenant(tenantId);
    }

    /**
     * Get warehouse utilization summary (e.g. where one wants to see how full each warehouse is)
     *
     * @param tenantId    tenant id
     * @param warehouseId warehouse id
     */
    @Transactional(readOnly = true)
    public WarehouseUtilization getSingleWarehouseUtilization(UUID tenantId, UUID warehouseId) {
        return storageRepository.getSingleWarehouseUtilizationByTenant(tenantId, warehouseId);
    }

    /**
     * Get units by warehouse
     *
     * @param tenantId    id of the owner of the warehouse
     * @param warehouseId id of the warehouse to be queried
     * @return a list of units in the warehouse
     */
    @Transactional(readOnly = true)
    public List<StorageUnitResponse> getStorageUnitsByWarehouse(UUID tenantId, UUID warehouseId) {
        List<StorageUnit> units = storageRepository.findByWarehouseIdAndWarehouse_TenantId(warehouseId, tenantId);
        return units.stream()
                .map(storageMapper::toDto)
                .toList();
    }

    /**
     * Get storage units
     *
     * @param tenantId tenant id
     * @return a list of storage unit of the given tenant
     */
    @Transactional(readOnly = true)
    public List<StorageUnitResponse> getAllStorageUnits(UUID tenantId) {
        List<StorageUnit> units = storageRepository.findByWarehouse_TenantId(tenantId);
        return units.stream()
                .map(storageMapper::toDto)
                .toList();
    }

    /**
     * Get single unit
     *
     * @param tenantId tenant id
     * @param unitId   storage unit id
     * @return returns a single storage unit or null
     */
    @Transactional(readOnly = true)
    public StorageUnitResponse getStorageUnit(UUID unitId, UUID tenantId) {
        StorageUnit unit = storageRepository.findUnitByTenantId(unitId, tenantId)
                .orElseThrow(() -> new NotFoundException("Storage unit not found"));

        return storageMapper.toDto(unit);
    }

    /**
     * Create storage unit
     *
     * @param tenantId tenant
     * @param request  inputs to create a unit
     * @return created storage unit
     */
    @Transactional
    public StorageUnitResponse createStorage(UUID tenantId, CreateStorageUnit request) {
        StorageUnit newStorage = new StorageUnit();

        newStorage.setUnitNumber(request.unitNumber());
        newStorage.setCapacityKg(request.capacityKg());
        newStorage.setWarehouseId(request.warehouseId());
        newStorage.setStatus(StorageStatus.AVAILABLE);
        newStorage.setCreatedAt(LocalDateTime.now());

        StorageUnit unit = storageRepository.save(newStorage);

        return storageMapper.toDto(unit);
    }

    /**
     * Update a storage unit
     *
     * @param tenantId tenant
     * @param unitId   tenant
     * @param request  update request fields
     * @return the updated storage unit
     */
    @Transactional
    public StorageUnitResponse updateUnit(UpdateUnit request,
                                          UUID unitId,
                                          UUID tenantId) {
        StorageUnit unit = storageRepository.findUnitByTenantId(unitId, tenantId)
                .orElseThrow(() -> new NotFoundException("Storage unit not found"));

        if (request.capacityKg() != null) {
            validateCapacityUpdate(unit, request.capacityKg());
            unit.setCapacityKg(request.capacityKg());
        }

        if (request.status() != null) {
            unit.setStatus(request.status());
        }

        StorageUnit updatedUnit = storageRepository.save(unit);
        return storageMapper.toDto(updatedUnit);
    }

    /**
     * delete a storage unit
     *
     * @param unitId   unit to be deleted
     * @param tenantId owner of unit
     */
    @Transactional
    public void deleteStorageUnit(UUID unitId, UUID tenantId) {
        StorageUnit unit = storageRepository
                .findUnitByTenantId(unitId, tenantId)
                .orElseThrow(() -> new NotFoundException("Storage Unit not found"));

        storageRepository.delete(unit);
    }

    // getWarehouseRevenue(UUID warehouseId)
    // getTopCustomersByCapacity(UUID tenantId)

    private void validateCapacityUpdate(StorageUnit unit, Integer newCapacity) {
        if (newCapacity <= 0) {
            throw new InvalidCapacityException("Capacity must be positive");
        }

        if (newCapacity < unit.getCapacityKg() &&
                (unit.getStatus() == StorageStatus.OCCUPIED || unit.getStatus() == StorageStatus.BOOKED)) {
            throw new CapacityReductionNotAllowedException("Cannot reduce capacity of occupied unit");
        }
    }

}
