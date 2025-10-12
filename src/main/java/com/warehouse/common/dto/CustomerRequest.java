package com.warehouse.common.dto;

import java.util.UUID;

public record CustomerRequest(
        String companyName,
        String contactEmail
) {
}
