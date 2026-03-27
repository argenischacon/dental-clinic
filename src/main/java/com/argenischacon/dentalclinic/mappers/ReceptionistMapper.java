package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistListDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistNestedDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistRequestDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistResponseDto;
import com.argenischacon.dentalclinic.model.Receptionist;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ReceptionistMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Receptionist toEntity(ReceptionistRequestDto dto);

    @Mapping(target = "username", source = "user.username")
    ReceptionistResponseDto toResponseDto(Receptionist entity);

    @Mapping(target = "fullName", expression = "java(entity.getName() + \" \" + entity.getLastName())")
    ReceptionistListDto toListDto(Receptionist entity);

    @Mapping(target = "fullName", expression = "java(entity.getName() + \" \" + entity.getLastName())")
    ReceptionistNestedDto toNestedDto(Receptionist entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    Receptionist updateEntityFromDto(ReceptionistRequestDto dto, @MappingTarget Receptionist entity);
}
