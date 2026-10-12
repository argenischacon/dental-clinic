package com.argenischacon.dentalclinic.dto.treatment;

import lombok.Builder;

@Builder
public record TreatmentStatsDto(
        long totalTreatments,
        long activeTreatments,
        long inactiveTreatments
) {}
