package com.argenischacon.dentalclinic.dto.receptionist;

public record ReceptionistStatsDto(
        long totalReceptionists,
        long activeReceptionists,
        long inactiveReceptionists
) {}
