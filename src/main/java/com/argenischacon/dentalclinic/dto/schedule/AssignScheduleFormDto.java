package com.argenischacon.dentalclinic.dto.schedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Builder
public class AssignScheduleFormDto {
    @NotNull(message = "El ID del dentista es obligatorio")
    private Long dentistId;

    @NotNull(message = "La duración es obligatoria")
    private Integer slotDurationMinutes = 30; //default

    @Valid
    private Map<DayOfWeek, DailyScheduleFormDto> schedules = new EnumMap<>(DayOfWeek.class);

    public AssignScheduleFormDto() {
        for(DayOfWeek day : DayOfWeek.values()){
            DailyScheduleFormDto daily = new DailyScheduleFormDto();
            daily.setDayOfWeek(day);
            daily.setAvailable(day != DayOfWeek.SUNDAY);
            schedules.put(day, daily);
        }
    }
}
