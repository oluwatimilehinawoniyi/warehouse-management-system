package com.warehouse.common.mapper;

import com.warehouse.bookings.entity.Booking;
import com.warehouse.common.dto.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "status", constant = "ACTIVE")
    Booking toEntity(BookingResponse dto);

    BookingResponse toDto(Booking entity);
}
