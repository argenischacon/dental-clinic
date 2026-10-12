package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.treatment.*;
import com.argenischacon.dentalclinic.model.Treatment;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TreatmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Treatment toEntity(TreatmentRequestDto dto);

    TreatmentResponseDto toResponseDto(Treatment entity);

    TreatmentListDto toListDto(Treatment entity);
    
    TreatmentRequestDto toRequestDto(Treatment entity);

    TreatmentNestedDto toNestedDto(Treatment entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Treatment updateEntityFromDto(TreatmentRequestDto dto, @MappingTarget Treatment entity);
}
