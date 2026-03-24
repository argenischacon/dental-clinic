package com.argenischacon.dentalclinic.dto.appointment;

import com.argenischacon.dentalclinic.dto.dentist.DentistNestedDto;
import com.argenischacon.dentalclinic.dto.patient.PatientNestedDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistNestedDto;
import com.argenischacon.dentalclinic.dto.service.ServiceNestedDto;
import com.argenischacon.dentalclinic.enums.AppointmentStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record AppointmentResponseDto(
        Long id,
        String appointmentCode,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        AppointmentStatus status,
        String notes,
        boolean active,
        ReceptionistNestedDto createdBy,
        DentistNestedDto dentist,
        PatientNestedDto patient,
        ServiceNestedDto service
) {}
