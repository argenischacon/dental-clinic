package com.argenischacon.dentalclinic.dto.treatment;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record TreatmentListDto(
        Long id,
        String treatmentCode,
        String name,
        BigDecimal price,
        int durationMinutes,
        boolean active
) {}
