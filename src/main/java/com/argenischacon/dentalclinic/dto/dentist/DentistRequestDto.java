package com.argenischacon.dentalclinic.dto.dentist;

import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DentistRequestDto(
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
        
        @NotBlank(message = "El numero de licencia es obligatorio")
        @Size(max = 20, message = "La matrícula no puede exceder 20 caracteres")
        String licenseNumber,
        
        @NotNull(message = "La especialidad es obligatoria")
        DentalSpecialty specialty,
        
        @NotNull(message = "La fecha de contratación es obligatoria")
        @PastOrPresent(message = "La fecha de contratación no puede ser futura")
        LocalDate hireDate,
        
        // User data
        @NotBlank(message = "El username es obligatorio")
        @Size(max = 50, message = "El usuario no puede exceder 50 caracteres")
        String username,
        
        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password
) {}
