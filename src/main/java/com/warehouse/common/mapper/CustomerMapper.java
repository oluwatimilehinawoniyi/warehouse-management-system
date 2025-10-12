package com.warehouse.common.mapper;

import com.warehouse.common.dto.CustomerResponse;
import com.warehouse.customers.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Customer toEntity(CustomerResponse dto);

    CustomerResponse toDto(Customer entity);
}
