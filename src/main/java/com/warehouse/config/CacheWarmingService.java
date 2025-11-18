package com.warehouse.config;

import com.warehouse.tenants.entity.Tenant;
import com.warehouse.tenants.repository.TenantRepository;
import com.warehouse.tenants.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CacheWarmingService {
    private final TenantService tenantService;
    private final TenantRepository tenantRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void warmCache() {
        log.info("Starting cache warming...");

        try {
            var tenantIds = tenantRepository.findAll()
                    .stream()
                    .map(Tenant::getId)
                    .toList();

            log.info("Warming cache for {} tenants", tenantIds.size());

            for (UUID tenantId : tenantIds) {
                try{
                    tenantService.getStats(tenantId);
                    log.debug("Warmed cache for tenant: {}", tenantId);
                } catch (Exception e){
                    log.warn("Failed to warm cache for tenant {}: {}", tenantId, e.getMessage());
                }
            }

            log.info("Cache warming completed successfully");
        } catch (Exception e) {
            log.error("Cache warming failed: {}", e.getMessage(), e);
        }
    }
}
