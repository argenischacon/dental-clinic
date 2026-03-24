package com.argenischacon.dentalclinic.dto.patient;

import com.argenischacon.dentalclinic.dto.guardian.GuardianNestedDto;
import lombok.Builder;
import java.time.LocalDate;

@Builder
public record PatientResponseDto(
        Long id,
        String dni,
        String name,
        String lastName,
        String email,
        String phone,
        String address,
        LocalDate birthDate,
        String patientCode,
        String bloodType,
        String allergies,
        String medicalNotes,
        int age,
        boolean isAdult,
        boolean active,
        GuardianNestedDto guardian
) {}
