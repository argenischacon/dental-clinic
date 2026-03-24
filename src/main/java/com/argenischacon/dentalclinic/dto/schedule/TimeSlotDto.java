package com.argenischacon.dentalclinic.dto.schedule;

import lombok.Builder;

import java.time.LocalTime;

@Builder
public record TimeSlotDto(
        LocalTime startTime,
        LocalTime endTime,
        boolean available
) {}
