package com.warehouse.tenants;

import com.warehouse.BaseIntegrationTest;
import com.warehouse.common.dto.WarehouseResponse;
import com.warehouse.tenants.entity.Tenant;
import com.warehouse.tenants.repository.TenantRepository;
import com.warehouse.warehouses.entity.Warehouse;
import com.warehouse.warehouses.repository.WarehouseRepository;
import com.warehouse.warehouses.service.WarehouseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MultiTenantIsolationTest extends BaseIntegrationTest {
    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private WarehouseService warehouseService;

    @Test
    void shouldIsolateTenantData_tenantACannotSeeTenantBData() {
        Tenant tenantA = new Tenant();
        tenantA.setCompanyName("Tenant A Company");
        tenantA.setEmail("tenantA-" + UUID.randomUUID() + "@test.com");  // Unique email
        tenantA = tenantRepository.save(tenantA);

        Tenant tenantB = new Tenant();
        tenantB.setCompanyName("Tenant B Company");
        tenantB.setEmail("tenantB-" + UUID.randomUUID() + "@test.com");  // Unique email
        tenantB = tenantRepository.save(tenantB);

        Warehouse warehouseA1 = new Warehouse();
        warehouseA1.setTenantId(tenantA.getId());
        warehouseA1.setName("Tenant A Warehouse 1");
        warehouseA1.setLocation("Lagos");
        warehouseRepository.save(warehouseA1);

        Warehouse warehouseA2 = new Warehouse();
        warehouseA2.setTenantId(tenantA.getId());
        warehouseA2.setName("Tenant A Warehouse 2");
        warehouseA2.setLocation("Abuja");
        warehouseRepository.save(warehouseA2);

        Warehouse warehouseB1 = new Warehouse();
        warehouseB1.setTenantId(tenantB.getId());
        warehouseB1.setName("Tenant B Warehouse 1");
        warehouseB1.setLocation("Kano");
        warehouseRepository.save(warehouseB1);

        List<WarehouseResponse> tenantAWarehouses = warehouseService.getWarehouses(tenantA.getId());
        assertThat(tenantAWarehouses).hasSize(2);
        assertThat(tenantAWarehouses)
                .extracting(WarehouseResponse::name)
                .containsExactlyInAnyOrder("Tenant A Warehouse 1", "Tenant A Warehouse 2");

        List<WarehouseResponse> tenantBWarehouses = warehouseService.getWarehouses(tenantB.getId());
        assertThat(tenantBWarehouses).hasSize(1);
        assertThat(tenantBWarehouses)
                .extracting(WarehouseResponse::name)
                .containsExactly("Tenant B Warehouse 1");

        assertThat(warehouseRepository.count()).isEqualTo(3);
    }

    @Test
    void shouldPreventCrossTenantAccess_tenantACannotAccessTenantBWarehouse() {
        Tenant tenantA = new Tenant();
        tenantA.setCompanyName("Tenant A");
        tenantA.setEmail("a@test.com");
        tenantA = tenantRepository.save(tenantA);

        Tenant tenantB = new Tenant();
        tenantB.setCompanyName("Tenant B");
        tenantB.setEmail("b@test.com");
        tenantB = tenantRepository.save(tenantB);

        Warehouse warehouseB = new Warehouse();
        warehouseB.setTenantId(tenantB.getId());
        warehouseB.setName("Tenant B Warehouse");
        warehouseB.setLocation("Port Harcourt");
        warehouseB = warehouseRepository.save(warehouseB);

        Warehouse finalWarehouseB = warehouseB;
        Tenant finalTenantA = tenantA;
        assertThatThrownBy(() ->
                warehouseService.getWarehouse(finalTenantA.getId(), finalWarehouseB.getId())
        ).isInstanceOf(com.warehouse.common.exceptions.NotFoundException.class)
                .hasMessageContaining("Warehouse not found");
    }
}
