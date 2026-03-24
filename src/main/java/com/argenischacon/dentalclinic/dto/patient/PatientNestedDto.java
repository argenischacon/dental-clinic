package com.argenischacon.dentalclinic.dto.patient;

import lombok.Builder;

@Builder
public record PatientNestedDto(
        Long id,
        String patientCode,
        String fullName,
        String dni
) {}
