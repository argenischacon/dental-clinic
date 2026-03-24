package com.argenischacon.dentalclinic.dto.appointment;

import com.argenischacon.dentalclinic.enums.AppointmentStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record AppointmentListDto(
        Long id,
        String appointmentCode,
        LocalDate date,
        LocalTime startTime,
        AppointmentStatus status,
        String patientFullName,
        String dentistFullName,
        String serviceName
) {}
