package com.argenischacon.dentalclinic.dto.schedule;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Builder
public record WorkScheduleRequestDto(
        @NotNull(message = "El día de la semana es obligatorio")
        DayOfWeek dayOfWeek,

        @NotNull(message = "La hora de inicio es obligatoria")
        @DateTimeFormat(pattern = "HH:mm")
        LocalTime startTime,

        @NotNull(message = "La hora de fin es obligatoria")
        @DateTimeFormat(pattern = "HH:mm")
        LocalTime endTime,

        int slotDurationMinutes,

        boolean available,

        @NotNull(message = "El ID del dentista es obligatorio")
        Long dentistId,

        List<ScheduleBreakDto> breaks
) {
    @AssertTrue(message = "La hora de fin debe ser posterior a la hora de inicio")
    public boolean isEndTimeAfterStartTime() {
        if (startTime == null || endTime == null) return true;
        return endTime.isAfter(startTime);
    }
}
