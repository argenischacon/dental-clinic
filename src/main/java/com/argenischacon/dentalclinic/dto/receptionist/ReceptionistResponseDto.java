package com.argenischacon.dentalclinic.dto.receptionist;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record ReceptionistResponseDto(
        Long id,
        String dni,
        String name,
        String lastName,
        String email,
        String phone,
        String address,
        LocalDate birthDate,
        String employeeNumber,
        LocalDate hireDate,
        String username,
        boolean active
) {}
