package com.argenischacon.dentalclinic.dto.service;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ServiceRequestDto(
        @NotBlank(message = "El código de servicio es obligatorio")
        @Size(max = 10, message = "El código no puede exceder 10 caracteres")
        String serviceCode,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
        String name,

        @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
        String description,

        @NotNull(message = "La duración es obligatoria")
        @Min(value = 1, message = "La duración mínima es 1 minuto")
        int durationMinutes,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
        BigDecimal price
) {}
