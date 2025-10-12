package com.warehouse.customers.repository;

import com.warehouse.common.dto.CustomerRequest;
import com.warehouse.common.dto.UpdateCustomer;
import com.warehouse.customers.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomersRepository extends JpaRepository<Customer, UUID> {
    List<Customer> findByTenantId(UUID tenantId);

    Optional<Customer> findByTenantIdAndCustomerId(UUID tenantId, UUID customerId);
}
