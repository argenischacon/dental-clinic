package com.argenischacon.dentalclinic.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyScheduleFormDto {
    private Long id;
    private DayOfWeek dayOfWeek;

    @DateTimeFormat (pattern = "HH:mm")
    private LocalTime startTime;

    @DateTimeFormat (pattern = "HH:mm")
    private LocalTime endTime;

    private boolean available;

    @Valid
    @Size(max = 3, message = "Un horario puede tener máximo 3 descansos")
    private List<ScheduleBreakFormDto> breaks = new ArrayList<>();
}
