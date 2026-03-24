package com.argenischacon.dentalclinic.dto.service;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record ServiceListDto(
        Long id,
        String serviceCode,
        String name,
        BigDecimal price,
        int durationMinutes,
        boolean active
) {}
