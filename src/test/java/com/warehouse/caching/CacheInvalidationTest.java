package com.warehouse.caching;

import com.warehouse.BaseIntegrationTest;
import com.warehouse.common.dto.CreateWarehouse;
import com.warehouse.common.dto.TenantStatResponse;
import com.warehouse.tenants.entity.Tenant;
import com.warehouse.tenants.repository.TenantRepository;
import com.warehouse.tenants.service.TenantService;
import com.warehouse.warehouses.service.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CacheInvalidationTest extends BaseIntegrationTest {
    @Autowired
    private TenantService tenantService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private TenantRepository tenantRepository;

    private UUID tenantId;

    @BeforeEach
    @Transactional
    void setup() {
        Tenant tenant = new Tenant();
        tenant.setCompanyName("Test Tenant");
        tenant.setEmail("cache@test.com");
        tenant = tenantRepository.save(tenant);
        tenantId = tenant.getId();
    }

    @Test
    void shouldInvalidateCache_whenWarehouseIsCreated() {
        TenantStatResponse initialStats = tenantService.getStats(tenantId);
        assertThat(initialStats.totalWarehouses()).isEqualTo(0);

        CreateWarehouse warehouse1 = new CreateWarehouse(
                "First Warehouse", "Lagos"
        );
        warehouseService.createWarehouse(tenantId, warehouse1);

        TenantStatResponse updatedStats = tenantService.getStats(tenantId);
        assertThat(updatedStats.totalWarehouses()).isEqualTo(1);

        CreateWarehouse warehouse2 = new CreateWarehouse(
                "Second Warehouse", "Ondo"
        );
        warehouseService.createWarehouse(tenantId, warehouse2);

        TenantStatResponse finalStats = tenantService.getStats(tenantId);
        assertThat(finalStats.totalWarehouses()).isEqualTo(2);
    }
}
