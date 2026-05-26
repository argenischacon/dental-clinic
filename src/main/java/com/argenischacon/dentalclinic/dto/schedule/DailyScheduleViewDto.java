package com.argenischacon.dentalclinic.dto.schedule;

import java.time.LocalTime;
import java.util.List;

public record DailyScheduleViewDto(
        boolean available,
        LocalTime startTime,
        LocalTime endTime,
        List<PreviewItemDto> items
) {
}
