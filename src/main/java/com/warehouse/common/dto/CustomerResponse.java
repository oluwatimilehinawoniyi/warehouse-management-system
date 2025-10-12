package com.warehouse.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        UUID tenantId,
        String companyName,
        String contactEmail,
        LocalDateTime createdAt
) {
}
