package com.argenischacon.dentalclinic.service.admin;

import com.argenischacon.dentalclinic.dto.admin.AdminDashboardMetricsDto;
import com.argenischacon.dentalclinic.repository.DentistRepository;
import com.argenischacon.dentalclinic.repository.ReceptionistRepository;
import com.argenischacon.dentalclinic.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final DentistRepository dentistRepository;
    private final ReceptionistRepository receptionistRepository;
    private final ServiceRepository serviceRepository;

    @Transactional(readOnly = true)
    public AdminDashboardMetricsDto getDashboardMetrics() {
        return new AdminDashboardMetricsDto(
                dentistRepository.count(),
                dentistRepository.countByActiveTrue(),
                receptionistRepository.count(),
                receptionistRepository.countByActiveTrue(),
                serviceRepository.count(),
                serviceRepository.countByActiveTrue()
        );
    }
}
