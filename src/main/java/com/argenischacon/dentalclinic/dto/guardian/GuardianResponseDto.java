package com.argenischacon.dentalclinic.dto.guardian;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record GuardianResponseDto(
        Long id,
        String dni,
        String name,
        String lastName,
        String email,
        String phone,
        String address,
        LocalDate birthDate,
        String relationship,
        boolean active
) {}
