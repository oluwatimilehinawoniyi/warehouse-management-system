package com.warehouse.customers.controller;

import com.warehouse.common.dto.CustomerRequest;
import com.warehouse.common.dto.UpdateCustomer;
import com.warehouse.common.response.ResponseHandler;
import com.warehouse.customers.service.CustomerService;
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
@RequestMapping("/api/v1/customers")
@Tag(
        name="Customers",
        description = "Individuals who book storage units in warehouses"
)
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("?tenantId={id}")
    public ResponseEntity<Object> getCustomers(
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Customers successfully returned",
                HttpStatus.OK,
                customerService.getCustomers(tenantId)
        );
    }

    @GetMapping("/{customerId}?tenantId={tenantId}")
    public ResponseEntity<Object> getCustomer(
            @PathVariable UUID customerId,
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Customer successfully returned",
                HttpStatus.OK,
                customerService.getCustomer(customerId, tenantId)
        );
    }

    @PostMapping()
    public ResponseEntity<Object> createCustomer(
            @Valid @RequestBody CustomerRequest request,
            @RequestParam UUID tenantId
    ) {
        return ResponseHandler.responseBuilder(
                "Customer successfully created",
                HttpStatus.CREATED,
                customerService.createCustomer(tenantId, request)
        );
    }

    @PatchMapping("/{customerId}?tenantId={tenantId}")
    public ResponseEntity<Object> updateCustomer(
            @RequestBody UpdateCustomer request,
            @RequestParam UUID tenantId,
            @PathVariable UUID customerId
    ) {
        return ResponseHandler.responseBuilder(
                "Customer successfully updated",
                HttpStatus.OK,
                customerService.updateCustomer(tenantId, customerId, request)
        );
    }

    @DeleteMapping("/{customerId}?tenantId={id}")
    public ResponseEntity<Object> deleteCustomer(
            @PathVariable UUID customerId,
            @RequestParam UUID tenantId
    ) {
        customerService.deleteCustomer(customerId, tenantId);
        return ResponseHandler.responseBuilder(
                "Customer successfully deleted",
                HttpStatus.NO_CONTENT,
                Map.of("success", true)
        );
    }
}
