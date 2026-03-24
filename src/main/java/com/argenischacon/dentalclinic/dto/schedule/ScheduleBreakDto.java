package com.argenischacon.dentalclinic.dto.schedule;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalTime;

@Builder
public record ScheduleBreakDto(
        @NotNull(message = "La hora de inicio del descanso es obligatoria")
        LocalTime startBreak,

        @NotNull(message = "La hora de fin del descanso es obligatoria")
        LocalTime endBreak,

        @NotBlank(message = "La etiqueta del descanso es obligatoria")
        @Size(max = 50, message = "La etiqueta no puede exceder 50 caracteres")
        String label
) {}
