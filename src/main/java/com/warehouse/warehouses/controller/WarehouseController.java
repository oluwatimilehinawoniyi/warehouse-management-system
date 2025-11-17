package com.warehouse.warehouses.controller;

import com.warehouse.common.dto.CreateWarehouse;
import com.warehouse.common.dto.UpdateWarehouse;
import com.warehouse.common.response.ResponseHandler;
import com.warehouse.warehouses.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    // Get all warehouses for tenant
    @GetMapping
    public ResponseEntity<Object> getWarehouses(@RequestParam UUID tenantId) {
        return ResponseHandler.responseBuilder(
                "Warehouses successfully returned",
                HttpStatus.OK,
                warehouseService.getWarehouses(tenantId)
        );
    }

    // Get specific warehouse
    @GetMapping("/{warehouseId}")
    public ResponseEntity<Object> getWarehouse(
            @PathVariable UUID warehouseId,
            @RequestParam UUID tenantId) {
        return ResponseHandler.responseBuilder(
                "Warehouse successfully returned",
                HttpStatus.OK,
                warehouseService.getWarehouse(tenantId, warehouseId)
        );
    }

    // Create warehouse
    @PostMapping
    public ResponseEntity<Object> createWarehouse(
            @Valid @RequestBody CreateWarehouse request,
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Warehouse successfully created",
                HttpStatus.CREATED,
                warehouseService.createWarehouse(tenantId, request)
        );
    }

    // Update warehouse
    @PatchMapping("/{warehouseId}?tenantId={tenantId}")
    public ResponseEntity<Object> updateWarehouse(
            @RequestBody UpdateWarehouse request,
            @PathVariable UUID warehouseId,
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Warehouse successfully updated",
                HttpStatus.OK,
                warehouseService.updateWarehouse(tenantId, warehouseId, request)
        );
    }

    // Delete warehouse (if no storage units)
    @DeleteMapping("/{warehouseId}?tenantId={tenantId}")
    public ResponseEntity<Object> deleteWarehouse(
            @PathVariable UUID warehouseId,
            @RequestParam UUID tenantId
    ) {
        warehouseService.deleteWarehouse(tenantId, warehouseId);
        return ResponseHandler.responseBuilder(
                "Warehouse successfully deleted",
                HttpStatus.NO_CONTENT,
                Map.of("success", true)
        );
    }

//    // Get warehouse utilization metrics
//    GET /warehouses/{warehouseId}/utilization?tenantId={id}
}
