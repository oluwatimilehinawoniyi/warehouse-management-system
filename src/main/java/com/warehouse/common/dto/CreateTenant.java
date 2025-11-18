package com.warehouse.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create a new tenant (warehouse company)")
public record CreateTenant(
        @Schema(description = "Name of the warehouse company", example = "Lagos Warehouse Solutions")
        @NotBlank(message = "Company name is required")
        String companyName,

        @Schema(description = "Contact email for the tenant", example = "contact@lagoswarehouse.com")
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email
) {}