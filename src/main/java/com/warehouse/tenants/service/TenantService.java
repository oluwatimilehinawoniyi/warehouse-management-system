package com.warehouse.tenants.service;

import com.warehouse.common.dto.CreateTenant;
import com.warehouse.common.dto.TenantResponse;
import com.warehouse.common.dto.TenantStatResponse;
import com.warehouse.common.exceptions.NotFoundException;
import com.warehouse.common.mapper.TenantMapper;
import com.warehouse.tenants.entity.Tenant;
import com.warehouse.tenants.repository.TenantRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    @Cacheable(value = "tenantStats", key = "#tenantId")
    public TenantStatResponse getStats(UUID tenantId) {
        return tenantRepository.getStatisticsForTenant(tenantId);
    }

    /**
     * register a new tenant
     * @param request new tenant information
     * @return dto of new tenant
     */
    public Object createTenant(CreateTenant request) {
        Tenant tenant = new Tenant();
        tenant.setCompanyName(request.companyName());
        tenant.setEmail(request.email());
        tenant.setCreatedAt(LocalDateTime.now());

        Tenant saved = tenantRepository.save(tenant);
        return tenantMapper.toDto(saved);
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
