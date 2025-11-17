package com.warehouse.common.mapper;

import com.warehouse.common.dto.StorageUnitResponse;
import com.warehouse.storage.entity.StorageUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StorageMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", constant = "AVAILABLE")
    StorageUnit toEntity(StorageUnitResponse dto);

    StorageUnitResponse toDto(StorageUnit entity);
}
