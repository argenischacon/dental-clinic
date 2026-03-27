package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.dentist.DentistListDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistNestedDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistRequestDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistResponseDto;
import com.argenischacon.dentalclinic.model.Dentist;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DentistMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "workSchedules", ignore = true)
    Dentist toEntity(DentistRequestDto dto);

    @Mapping(target = "username", source = "user.username")
    DentistResponseDto toResponseDto(Dentist entity);

    @Mapping(target = "fullName", expression = "java(entity.getName() + \" \" + entity.getLastName())")
    DentistListDto toListDto(Dentist entity);

    @Mapping(target = "fullName", expression = "java(entity.getName() + \" \" + entity.getLastName())")
    DentistNestedDto toNestedDto(Dentist entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "workSchedules", ignore = true)
    Dentist updateEntityFromDto(DentistRequestDto dto, @MappingTarget Dentist entity);
}
