package com.argenischacon.dentalclinic.dto.guardian;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record GuardianRequestDto(
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

        @NotBlank(message = "El parentesco es obligatorio")
        @Size(max = 50, message = "El parentesco no puede exceder 50 caracteres")
        String relationship
) {}
