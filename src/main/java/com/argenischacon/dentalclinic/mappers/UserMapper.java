package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.user.UserNestedDto;
import com.argenischacon.dentalclinic.dto.user.UserRequestDto;
import com.argenischacon.dentalclinic.dto.user.UserResponseDto;
import com.argenischacon.dentalclinic.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(UserRequestDto dto);

    UserResponseDto toResponseDto(User entity);

    UserNestedDto toNestedDto(User entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User updateEntityFromDto(UserRequestDto dto, @MappingTarget User entity);
}
