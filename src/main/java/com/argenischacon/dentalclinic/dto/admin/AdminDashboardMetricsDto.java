package com.argenischacon.dentalclinic.dto.admin;

public record AdminDashboardMetricsDto(
        long totalDentists,
        long activeDentists,
        long totalReceptionists,
        long activeReceptionists,
        long totalServices,
        long activeServices
) {
}
