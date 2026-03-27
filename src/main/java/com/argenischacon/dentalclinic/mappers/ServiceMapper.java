package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.service.ServiceNestedDto;
import com.argenischacon.dentalclinic.dto.service.ServiceRequestDto;
import com.argenischacon.dentalclinic.dto.service.ServiceResponseDto;
import com.argenischacon.dentalclinic.model.Service;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Service toEntity(ServiceRequestDto dto);

    ServiceResponseDto toResponseDto(Service entity);

    ServiceNestedDto toNestedDto(Service entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Service updateEntityFromDto(ServiceRequestDto dto, @MappingTarget Service entity);
}
