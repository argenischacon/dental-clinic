package com.argenischacon.dentalclinic.dto.dentist;

import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DentistResponseDto(
        Long id,
        String dni,
        String name,
        String lastName,
        String email,
        String phone,
        String address,
        String username,
        LocalDate birthDate,
        String licenseNumber,
        DentalSpecialty specialty,
        LocalDate hireDate,
        boolean active
) {}
