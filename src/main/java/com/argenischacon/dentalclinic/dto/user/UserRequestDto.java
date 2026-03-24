package com.argenischacon.dentalclinic.dto.user;

import com.argenischacon.dentalclinic.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UserRequestDto(
        @NotBlank(message = "El username es obligatorio")
        @Size(max = 50, message = "El usuario no puede exceder 50 caracteres")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String password,

        @NotNull(message = "El rol es obligatorio")
        Role role
) {}
