package com.argenischacon.dentalclinic.dto.dentist;

public record DentistStatsDto(
        long totalDentists,
        long activeDentists,
        long inactiveDentists
) {}
