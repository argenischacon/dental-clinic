package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.guardian.GuardianListDto;
import com.argenischacon.dentalclinic.dto.guardian.GuardianNestedDto;
import com.argenischacon.dentalclinic.dto.guardian.GuardianRequestDto;
import com.argenischacon.dentalclinic.dto.guardian.GuardianResponseDto;
import com.argenischacon.dentalclinic.model.Guardian;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface GuardianMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patients", ignore = true)
    Guardian toEntity(GuardianRequestDto dto);

    GuardianResponseDto toResponseDto(Guardian entity);

    @Mapping(target = "fullName", expression = "java(entity.getName() + \" \" + entity.getLastName())")
    GuardianListDto toListDto(Guardian entity);

    @Mapping(target = "fullName", expression = "java(entity.getName() + \" \" + entity.getLastName())")
    GuardianNestedDto toNestedDto(Guardian entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "patients", ignore = true)
    Guardian updateEntityFromDto(GuardianRequestDto dto, @MappingTarget Guardian entity);
}
