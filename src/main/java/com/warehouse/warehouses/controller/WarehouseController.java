package com.warehouse.warehouses.controller;

import com.warehouse.common.dto.CreateWarehouse;
import com.warehouse.common.dto.UpdateWarehouse;
import com.warehouse.common.response.ResponseHandler;
import com.warehouse.warehouses.service.WarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(
        name="Warehouses",
        description = "Physical warehouse location management for tenants"
)
public class WarehouseController {

    private final WarehouseService warehouseService;

    // Get all warehouses for tenant
    @Operation(
            summary = "Get warehouses",
            description = "Get all physical warehouses for the specified tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Warehouse successfully returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    @GetMapping
    public ResponseEntity<Object> getWarehouses(@RequestParam UUID tenantId) {
        return ResponseHandler.responseBuilder(
                "Warehouses successfully returned",
                HttpStatus.OK,
                warehouseService.getWarehouses(tenantId)
        );
    }

    // Get specific warehouse
    @Operation(
            summary = "Get a warehouse",
            description = "Get a specified warehouse for the specified tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Warehouse successfully returned"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    @GetMapping("/{warehouseId}")
    public ResponseEntity<Object> getWarehouse(
            @Parameter(
                    description = "ID of the warehouse",
                    required = true
            )
            @PathVariable UUID warehouseId,
            @Parameter(
                    description = "ID of the tenant (warehouse company). In production, this would come from JWT claims rather than a query parameter",
                    required = true
            )
            @RequestParam UUID tenantId
            ) {
        return ResponseHandler.responseBuilder(
                "Warehouse successfully returned",
                HttpStatus.OK,
                warehouseService.getWarehouse(tenantId, warehouseId)
        );
    }

    // Create warehouse
    @Operation(
            summary = "Create a new warehouse",
            description = "Creates a physical warehouse location for the specified tenant. The warehouse will be empty initially with 0 storage units."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Warehouse created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Tenant not found")
    })
    @PostMapping
    public ResponseEntity<Object> createWarehouse(
            @Valid @RequestBody CreateWarehouse request,
            @Parameter(
                    description = "ID of the tenant (warehouse company). In production, this would come from JWT claims rather than a query parameter",
                    required = true
            )
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Warehouse successfully created",
                HttpStatus.CREATED,
                warehouseService.createWarehouse(tenantId, request)
        );
    }

    // Update warehouse
    @Operation(
            summary = "Update a warehouse",
            description = "Update a physical warehouse for the specified tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Warehouse successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Tenant not found"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    @PatchMapping("/{warehouseId}?tenantId={tenantId}")
    public ResponseEntity<Object> updateWarehouse(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Values to be updated (name and location of warehouse).",
                    required = true
            )
            @RequestBody UpdateWarehouse request,
            @Parameter(
                    description = "ID of the warehouse",
                    required = true
            )
            @PathVariable UUID warehouseId,
            @Parameter(
                    description = "ID of the tenant (warehouse company). In production, this would come from JWT claims rather than a query parameter",
                    required = true
            )
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Warehouse successfully updated",
                HttpStatus.OK,
                warehouseService.updateWarehouse(tenantId, warehouseId, request)
        );
    }

    // Delete warehouse (if no storage units)
    @Operation(
            summary = "Delete a warehouse",
            description = "Delete a physical warehouse for the specified tenant."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Warehouse successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Tenant not found"),
            @ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    @DeleteMapping("/{warehouseId}?tenantId={tenantId}")
    public ResponseEntity<Object> deleteWarehouse(
            @Parameter(
                    description = "ID of the warehouse",
                    required = true
            )
            @PathVariable UUID warehouseId,
            @Parameter(
                    description = "ID of the tenant (warehouse company). In production, this would come from JWT claims rather than a query parameter",
                    required = true
            )
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
