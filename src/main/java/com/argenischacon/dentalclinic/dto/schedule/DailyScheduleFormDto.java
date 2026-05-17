package com.argenischacon.dentalclinic.dto.schedule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyScheduleFormDto {
    private DayOfWeek dayOfWeek;

    @DateTimeFormat (pattern = "HH:mm")
    private LocalTime startTime;

    @DateTimeFormat (pattern = "HH:mm")
    private LocalTime endTime;

    private boolean available;
}
