package com.argenischacon.dentalclinic.dto.user;

import com.argenischacon.dentalclinic.enums.Role;
import lombok.Builder;

@Builder
public record UserResponseDto(
        Long id,
        String username,
        Role role,
        boolean active,
        boolean mustChangePassword
) {}
