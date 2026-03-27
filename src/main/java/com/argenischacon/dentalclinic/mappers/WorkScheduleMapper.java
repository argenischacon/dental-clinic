package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.schedule.ScheduleBreakDto;
import com.argenischacon.dentalclinic.dto.schedule.WorkScheduleRequestDto;
import com.argenischacon.dentalclinic.dto.schedule.WorkScheduleResponseDto;
import com.argenischacon.dentalclinic.model.ScheduleBreak;
import com.argenischacon.dentalclinic.model.WorkSchedule;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "available", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "dentist", ignore = true)
    @Mapping(target = "breaks", ignore = true)
    WorkSchedule toEntity(WorkScheduleRequestDto dto);

    WorkScheduleResponseDto toResponseDto(WorkSchedule entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "workSchedule", ignore = true)
    ScheduleBreak toBreakEntity(ScheduleBreakDto dto);

    ScheduleBreakDto toBreakDto(ScheduleBreak entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "available", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "dentist", ignore = true)
    @Mapping(target = "breaks", ignore = true)
    WorkSchedule updateEntityFromDto(WorkScheduleRequestDto dto, @MappingTarget WorkSchedule entity);
}
