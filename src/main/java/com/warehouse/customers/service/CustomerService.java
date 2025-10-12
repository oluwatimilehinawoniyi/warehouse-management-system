package com.warehouse.customers.service;

import com.warehouse.common.dto.CustomerRequest;
import com.warehouse.common.dto.CustomerResponse;
import com.warehouse.common.dto.UpdateCustomer;
import com.warehouse.common.exceptions.NotFoundException;
import com.warehouse.common.mapper.CustomerMapper;
import com.warehouse.customers.entity.Customer;
import com.warehouse.customers.repository.CustomersRepository;
import com.warehouse.tenants.entity.Tenant;
import com.warehouse.tenants.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomersRepository customersRepository;
    private final CustomerMapper customerMapper;
    private final TenantRepository tenantRepository;

    /**
     * gets a list of a tenant's customer
     *
     * @param tenantId the tenant
     * @return a list of customers belonging to the tenant
     */
    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomers(UUID tenantId) {
        List<Customer> customers = customersRepository.findByTenantId(tenantId);
        return customers
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    /**
     * get a customer of a tenant
     *
     * @param customerId the customer
     * @param tenantId   the tenant
     * @return a customer belonging to the tenant
     */
    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(UUID customerId, UUID tenantId) {
        Customer customer = customersRepository
                .findByTenantIdAndCustomerId(tenantId, customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));

        return customerMapper.toDto(customer);
    }

    /**
     * get a customer of a tenant
     *
     * @param request  the details of a new customer
     * @param tenantId the tenant
     * @return a customer belonging to the tenant
     */
    @Transactional
    public CustomerResponse createCustomer(
            UUID tenantId,
            CustomerRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found. Customer can't be created."));

        Customer customer = new Customer();

        customer.setCompanyName(request.companyName());
        customer.setContactEmail(request.contactEmail());
        customer.setCreatedAt(LocalDateTime.now());
        customer.setTenantId(tenant.getId());

        Customer newCustomer = customersRepository.save(customer);
        return customerMapper.toDto(newCustomer);
    }


    /**
     * update a customer
     *
     * @param request  the details of a new customer
     * @param tenantId the tenant
     * @return updated customer
     */
    @Transactional
    public CustomerResponse updateCustomer(
            UUID tenantId,
            UUID customerId,
            UpdateCustomer request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found. Customer can't be updated."));

        Customer customer = customersRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found. Customer details can't be updated."));

        customer.setCompanyName(request.companyName());
        customer.setContactEmail(request.contactEmail());

        Customer updatedCustomer = customersRepository.save(customer);
        return customerMapper.toDto(updatedCustomer);
    }

    /**
     * delete a customer
     *
     * @param customerId the customer
     * @param tenantId   the tenant
     */
    @Transactional
    public void deleteCustomer(UUID customerId, UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new NotFoundException("Tenant not found. Customer can't be deleted."));

        Customer customer = customersRepository.findByTenantIdAndCustomerId(tenantId, customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found."));

        customersRepository.delete(customer);
    }
}
