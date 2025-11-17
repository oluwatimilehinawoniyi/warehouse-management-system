package com.warehouse.tenants.controller;

import com.warehouse.common.response.ResponseHandler;
import com.warehouse.tenants.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants")
@Tag(
        name="Tenants",
        description = "Owners of warehouses available for bookings by Customers"
)
public class TenantController {
    private final TenantService tenantService;

    // get tenant profile (self-lookup)
    @Operation(
            summary = "Get tenant's profile",
            description = "Get tenant's profile information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile successfully returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    @GetMapping("/profile?tenant={tenantId}")
    public ResponseEntity<Object> getProfile(
            @Parameter(
                    description = "ID of the tenant (warehouse company). In production, this would come from JWT claims rather than a query parameter",
                    required = true
            )
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Profile successfully returned",
                HttpStatus.OK,
                tenantService.getProfile(tenantId)
        );
    }

    // Get tenant statistics/dashboard data
    @Operation(
            summary = "Get tenant's statistics",
            description = "Get statistics of tenant's ops/business, including warehouses, bookings, storage units and customers"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stats successfully returned"),
//            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    @GetMapping("/stats?tenantId={tenantId}")
    public ResponseEntity<Object> stats(
            @Parameter(
                    description = "ID of the tenant (warehouse company). In production, this would come from JWT claims rather than a query parameter",
                    required = true
            )
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Stats successfully returned",
                HttpStatus.OK,
                tenantService.getStats(tenantId)
        );
    }

    //    GET /tenants/dashboard?tenantId={id}
//    @GetMapping("/dashboard?tenantId={tenantId}")
//    public ResponseEntity<Object> dashboard(
//            @RequestParam UUID tenantId
//    ) {
//        return ResponseHandler.responseBuilder(
//                "Dashboard info successfully returned",
//                HttpStatus.OK,
//                tenantService.getDashboard(tenantId)
//        );
//    }

// Tenant settings
//    PATCH /tenants/settings?tenantId={id}
//    Body: { notifications?, timezone?, currency? }
}
