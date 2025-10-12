package com.warehouse.common.mapper;

import com.warehouse.common.dto.TenantResponse;
import com.warehouse.tenants.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Tenant toEntity(TenantResponse dto);

    TenantResponse toDto(Tenant entity);
}
