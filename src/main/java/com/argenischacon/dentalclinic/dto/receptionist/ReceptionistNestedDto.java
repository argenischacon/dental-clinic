package com.argenischacon.dentalclinic.dto.receptionist;

import lombok.Builder;

@Builder
public record ReceptionistNestedDto(
        Long id,
        String fullName,
        String employeeNumber
) {}
