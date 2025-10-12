package com.warehouse.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WarehouseResponse(
        UUID id,
        UUID tenantId,
        String name,
        String location,
        int totalUnits,
        LocalDateTime createdAt
) {
}
