package com.argenischacon.dentalclinic.dto.service;

import lombok.Builder;

@Builder
public record ServiceStatsDto(
        long totalServices,
        long activeServices,
        long inactiveServices
) {}
