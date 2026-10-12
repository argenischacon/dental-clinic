package com.argenischacon.dentalclinic.dto.treatment;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record TreatmentNestedDto(
        Long id,
        String name,
        BigDecimal price
) {}
