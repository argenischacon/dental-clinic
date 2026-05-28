package com.argenischacon.dentalclinic.dto.service;

import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record ServiceResponseDto(
        Long id,
        String serviceCode,
        String name,
        String description,
        int durationMinutes,
        BigDecimal price,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
