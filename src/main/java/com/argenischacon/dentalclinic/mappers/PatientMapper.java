package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.patient.PatientListDto;
import com.argenischacon.dentalclinic.dto.patient.PatientNestedDto;
import com.argenischacon.dentalclinic.dto.patient.PatientRequestDto;
import com.argenischacon.dentalclinic.dto.patient.PatientResponseDto;
import com.argenischacon.dentalclinic.model.Patient;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {GuardianMapper.class})
public interface PatientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "guardian", ignore = true)
    Patient toEntity(PatientRequestDto dto);

    @Mapping(target = "age", expression = "java(entity.getAge())")
    @Mapping(target = "isAdult", expression = "java(entity.isAdult())")
    PatientResponseDto toResponseDto(Patient entity);

    @Mapping(target = "fullName", expression = "java(entity.getName() + \" \" + entity.getLastName())")
    @Mapping(target = "age", expression = "java(entity.getAge())")
    PatientListDto toListDto(Patient entity);

    @Mapping(target = "fullName", expression = "java(entity.getName() + \" \" + entity.getLastName())")
    PatientNestedDto toNestedDto(Patient entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "guardian", ignore = true)
    Patient updateEntityFromDto(PatientRequestDto dto, @MappingTarget Patient entity);
}
