package com.argenischacon.dentalclinic.dto.service;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record ServiceNestedDto(
        Long id,
        String name,
        BigDecimal price
) {}
