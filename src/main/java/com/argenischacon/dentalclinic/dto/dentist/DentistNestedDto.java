package com.argenischacon.dentalclinic.dto.dentist;

import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import lombok.Builder;

@Builder
public record DentistNestedDto(
        Long id,
        String fullName,
        DentalSpecialty specialty
) {}
