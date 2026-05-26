package com.argenischacon.dentalclinic.dto.schedule;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleBreakFormDto {
    private Long id;

    @NotNull(message = "La hora de inicio del descanso es obligatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startBreak;

    @NotNull(message = "La hora de fin del descanso es obligatoria")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endBreak;

    @NotBlank(message = "La etiqueta del descanso es obligatoria")
    @Size(max = 50, message = "La etiqueta no puede exceder 50 caracteres")
    private String label;

    @AssertTrue(message = "La hora de inicio del descanso debe ser anterior a la hora de fin.")
    public boolean isValidTimeRange() {
        if (startBreak == null || endBreak == null) {
            return true;
        }
        return startBreak.isBefore(endBreak);
    }
}
