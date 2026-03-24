package com.argenischacon.dentalclinic.dto.guardian;

import lombok.Builder;

@Builder
public record GuardianNestedDto(
        Long id,
        String fullName,
        String phone,
        String relationship
) {}
