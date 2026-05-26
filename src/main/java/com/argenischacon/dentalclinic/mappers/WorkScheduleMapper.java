package com.argenischacon.dentalclinic.mappers;

import com.argenischacon.dentalclinic.dto.schedule.ScheduleBreakDto;
import com.argenischacon.dentalclinic.dto.schedule.ScheduleBreakFormDto;
import com.argenischacon.dentalclinic.dto.schedule.WorkScheduleResponseDto;
import com.argenischacon.dentalclinic.model.ScheduleBreak;
import com.argenischacon.dentalclinic.model.WorkSchedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkScheduleMapper {
    WorkScheduleResponseDto toResponseDto(WorkSchedule entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "workSchedule", ignore = true)
    ScheduleBreak toBreakEntity(ScheduleBreakDto dto);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "workSchedule", ignore = true)
    ScheduleBreak toBreakEntity(ScheduleBreakFormDto dto);
    ScheduleBreakDto toBreakDto(ScheduleBreak entity);
}
