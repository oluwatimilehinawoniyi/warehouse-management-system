package com.warehouse.tenants.service;

import com.warehouse.common.dto.TenantResponse;
import com.warehouse.common.dto.TenantStatResponse;
import com.warehouse.common.exceptions.NotFoundException;
import com.warehouse.common.mapper.TenantMapper;
import com.warehouse.tenants.entity.Tenant;
import com.warehouse.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class TenantService {
    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;

    /**
     * get profile of the tenant
     *
     * @param tenantId tenant
     * @return tenant details
     */
    public TenantResponse getProfile(UUID tenantId) {
        Tenant tenant = tenantRepository
                .findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found"));

        return tenantMapper.toDto(tenant);
    }

    /**
     * get statistics of tenant's ops/business
     *
     * @param tenantId tenant
     * @return information of tenant's operation
     */
    public TenantStatResponse getStats(UUID tenantId) {
        return tenantRepository.getStatisticsForTenant(tenantId);
    }


    // Tenant's complete business view
//    Response: {
//        companyInfo,
//                warehouseSummary,
//                recentBookings,
//                expiringBookings,
//                revenueMetrics
//    }
//    public Object getDashboard(UUID tenantId) {
//    }
}
