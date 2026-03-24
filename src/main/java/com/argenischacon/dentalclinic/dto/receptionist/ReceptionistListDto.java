package com.argenischacon.dentalclinic.dto.receptionist;

import lombok.Builder;

@Builder
public record ReceptionistListDto(
        Long id,
        String employeeNumber,
        String dni,
        String fullName,
        String email,
        String phone,
        boolean active
) {}
