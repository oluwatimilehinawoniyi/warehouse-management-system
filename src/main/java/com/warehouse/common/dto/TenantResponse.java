package com.warehouse.common.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TenantResponse(
        UUID id,
        String companyName,
        String email,
        LocalDateTime createdAt
) {
}
