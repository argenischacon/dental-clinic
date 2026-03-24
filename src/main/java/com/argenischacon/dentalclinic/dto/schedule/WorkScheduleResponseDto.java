package com.argenischacon.dentalclinic.dto.schedule;

import lombok.Builder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Builder
public record WorkScheduleResponseDto(
        Long id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        int slotDurationMinutes,
        boolean available,
        List<ScheduleBreakDto> breaks
) {}
