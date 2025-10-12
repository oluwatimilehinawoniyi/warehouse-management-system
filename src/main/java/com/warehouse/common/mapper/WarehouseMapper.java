package com.warehouse.common.mapper;

import com.warehouse.common.dto.WarehouseResponse;
import com.warehouse.warehouses.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Warehouse toEntity(WarehouseResponse dto);

    WarehouseResponse toDto(Warehouse entity);
}
