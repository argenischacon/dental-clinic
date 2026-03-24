package com.argenischacon.dentalclinic.dto.guardian;

import lombok.Builder;

@Builder
public record GuardianListDto(
        Long id,
        String dni,
        String fullName,
        String email,
        String phone,
        String relationship,
        boolean active
) {}
