package com.argenischacon.dentalclinic.dto.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record PatientRequestDto(
        @NotBlank(message = "El DNI es obligatorio")
        @Size(max = 20, message = "El DNI no puede exceder 20 caracteres")
        String dni,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 60, message = "El nombre no puede exceder 60 caracteres")
        String name,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 60, message = "El apellido no puede exceder 60 caracteres")
        String lastName,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        @Size(max = 60, message = "El email no puede exceder 60 caracteres")
        String email,

        @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
        String phone,

        @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
        String address,

        @NotNull(message = "La fecha de nacimiento es obligatoria")
        @Past(message = "La fecha de nacimiento debe ser en el pasado")
        LocalDate birthDate,

        @NotBlank(message = "El código de paciente es obligatorio")
        @Size(max = 10, message = "El código no puede exceder 10 caracteres")
        String patientCode,

        @NotBlank(message = "El tipo de sangre es obligatorio")
        @Size(max = 5, message = "El tipo de sangre no puede exceder 5 caracteres")
        String bloodType,

        @Size(max = 500, message = "Las alergias no pueden exceder 500 caracteres")
        String allergies,

        @Size(max = 1000, message = "Las notas médicas no pueden exceder 1000 caracteres")
        String medicalNotes,

        Long guardianId
) {}
