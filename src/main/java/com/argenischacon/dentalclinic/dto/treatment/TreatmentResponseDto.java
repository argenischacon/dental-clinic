package com.argenischacon.dentalclinic.dto.treatment;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TreatmentResponseDto(
        Long id,
        String treatmentCode,
        String name,
        String description,
        int durationMinutes,
        BigDecimal price,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
