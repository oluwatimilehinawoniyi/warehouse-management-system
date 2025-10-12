package com.warehouse.tenants.controller;

import com.warehouse.common.response.ResponseHandler;
import com.warehouse.tenants.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tenants")
public class TenantController {
    private final TenantService tenantService;

    // get tenant profile (self-lookup)
    @GetMapping("/profile?tenant={tenantId}")
    public ResponseEntity<Object> getProfile(
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Profile successfully returned",
                HttpStatus.OK,
                tenantService.getProfile(tenantId)
        );
    }

    // Get tenant statistics/dashboard data
    @GetMapping("/stats?tenantId={tenantId}")
    public ResponseEntity<Object> stats(
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
