package com.argenischacon.dentalclinic.dto.dentist;

import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import lombok.Builder;

@Builder
public record DentistListDto(
        Long id,
        String name,
        String lastName,
        String licenseNumber,
        DentalSpecialty specialty,
        String email,
        String phone,
        boolean active
) {}
