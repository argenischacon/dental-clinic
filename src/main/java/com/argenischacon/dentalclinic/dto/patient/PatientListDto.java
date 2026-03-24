package com.argenischacon.dentalclinic.dto.patient;

import lombok.Builder;

@Builder
public record PatientListDto(
        Long id,
        String patientCode,
        String dni,
        String fullName,
        int age,
        String phone,
        boolean active
) {}
