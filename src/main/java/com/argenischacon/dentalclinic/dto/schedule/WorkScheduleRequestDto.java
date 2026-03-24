package com.argenischacon.dentalclinic.dto.schedule;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Builder
public record WorkScheduleRequestDto(
        @NotNull(message = "El día de la semana es obligatorio")
        DayOfWeek dayOfWeek,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime startTime,

        @NotNull(message = "La hora de fin es obligatoria")
        LocalTime endTime,

        int slotDurationMinutes,

        @NotNull(message = "El ID del dentista es obligatorio")
        Long dentistId,

        List<ScheduleBreakDto> breaks
) {}
