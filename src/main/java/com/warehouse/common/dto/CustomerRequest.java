package com.warehouse.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CustomerRequest(
        @NotBlank(message = "Company name is required")
        String companyName,

        @Email(message = "Invalid email format")
        @NotBlank(message = "Contact email is required")
        String contactEmail
) {
}
