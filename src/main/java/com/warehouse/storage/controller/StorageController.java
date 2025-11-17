package com.warehouse.storage.controller;

import com.warehouse.common.dto.CreateStorageUnit;
import com.warehouse.common.dto.StorageUnitResponse;
import com.warehouse.common.dto.UpdateUnit;
import com.warehouse.common.exceptions.InvalidCapacityException;
import com.warehouse.common.response.ResponseHandler;
import com.warehouse.storage.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/storages")
public class StorageController {
    private final StorageService storageService;

    @GetMapping("/available?tenantId={id}&minCapacity={kg}")
    public ResponseEntity<Object> getAvailableUnits(
            @RequestParam UUID id,
            @RequestParam int kg
    ) {
        return ResponseHandler.responseBuilder(
                "Available units successfully returned",
                HttpStatus.OK,
                storageService.getAvailableUnits(id, kg)
        );
    }

    @GetMapping("?tenantId={tenantId}&warehouseId={warehouseId}")
    public ResponseEntity<Object> getStorageUnits(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) UUID warehouseId
    ) {
        if (warehouseId != null) {
            return ResponseHandler.responseBuilder(
                    "All units successfully returned",
                    HttpStatus.OK,
                    storageService.getStorageUnitsByWarehouse(tenantId, warehouseId)
            );
        } else {
            return ResponseHandler.responseBuilder(
                    "All units successfully returned",
                    HttpStatus.OK,
                    storageService.getAllStorageUnits(tenantId)
            );
        }
    }


    @GetMapping("/{unitId}?tenantId={tenantId}")
    public ResponseEntity<Object> getStorageUnit(
            @RequestParam UUID tenantId,
            @PathVariable UUID unitId
    ) {
        return ResponseHandler.responseBuilder(
                "Storage Unit successfully returned",
                HttpStatus.OK,
                storageService.getStorageUnit(unitId, tenantId)
        );
    }


    @PostMapping
    public ResponseEntity<Object> createStorageUnit(@RequestParam UUID tenantId,
                                                    @Valid @RequestBody CreateStorageUnit request) {
        return ResponseHandler.responseBuilder(
                "Storage successfully created",
                HttpStatus.CREATED,
                storageService.createStorage(tenantId, request)
        );
    }


    @PatchMapping("/{unitId}?tenantId={tenantId}")
    public ResponseEntity<Object> updateStorageUnit(
            @RequestParam UUID tenantId,
            @PathVariable UUID unitId,
            @RequestBody UpdateUnit request
    ) {
        try {
            StorageUnitResponse response = storageService.updateUnit(request, unitId, tenantId);
            return ResponseHandler.responseBuilder("Updated successfully", HttpStatus.OK, response);
        } catch (InvalidCapacityException e) {
            return ResponseHandler.responseBuilder(e.getMessage(), HttpStatus.BAD_REQUEST, Map.of("message", "Capacity must be positive"));
        }
    }


    @DeleteMapping("/{unitId}?tenantId={tenantId}")
    public ResponseEntity<Object> deleteStorageUnit(
            @PathVariable UUID unitId,
            @RequestParam UUID tenantId
    ) {
        storageService.deleteStorageUnit(unitId, tenantId);
        return ResponseHandler.responseBuilder(
                "Storage unit successfully deleted",
                HttpStatus.NO_CONTENT,
                Map.of("success", true)
        );
    }
}
