package com.argenischacon.dentalclinic.dto.appointment;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record AppointmentRequestDto(
        @NotNull(message = "La fecha es obligatoria")
        @FutureOrPresent(message = "La fecha no puede ser en el pasado")
        LocalDate date,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime startTime,

        @NotNull(message = "El servicio es obligatorio")
        Long serviceId,

        @NotNull(message = "El paciente es obligatorio")
        Long patientId,

        @NotNull(message = "El dentista es obligatorio")
        Long dentistId,

        @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
        String notes
) {}
