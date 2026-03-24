package com.argenischacon.dentalclinic.dto.user;

import com.argenischacon.dentalclinic.enums.Role;
import lombok.Builder;

@Builder
public record UserNestedDto(
        Long id,
        String username,
        Role role
) {}
