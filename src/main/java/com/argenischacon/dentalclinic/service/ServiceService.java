package com.argenischacon.dentalclinic.service;

import com.argenischacon.dentalclinic.dto.service.*;
import com.argenischacon.dentalclinic.mappers.ServiceMapper;
import com.argenischacon.dentalclinic.model.Service;
import com.argenischacon.dentalclinic.repository.ServiceRepository;
import com.argenischacon.dentalclinic.specification.ServiceSpecification;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    @Transactional
    public void serviceAdd(ServiceRequestDto dto) {

        if (serviceRepository.findByServiceCode(dto.serviceCode()).isPresent()) {
            throw new BusinessRuleException("El código de servicio ya está registrado en el sistema");
        }

        if (serviceRepository.findByName(dto.name()).isPresent()) {
            throw new BusinessRuleException("El nombre del servicio ya está en uso");
        }

        Service service = serviceMapper.toEntity(dto);
        serviceRepository.save(service);
    }

    public Page<ServiceListDto> findAllServices(String search, Boolean active, Pageable pageable) {
        Specification<Service> spec = ServiceSpecification.build(search, active);
        return serviceRepository.findAll(spec, pageable).map(serviceMapper::toListDto);
    }

    public List<ServiceNestedDto> findAllActiveServices() {
        return serviceRepository.findAllByActiveTrue().stream()
                .map(serviceMapper::toNestedDto)
                .toList();
    }

    public ServiceStatsDto getStats() {
        long total = serviceRepository.count();
        long active = serviceRepository.countByActiveTrue();
        long inactive = serviceRepository.countByActiveFalse();
        return new ServiceStatsDto(total, active, inactive);
    }

    public ServiceResponseDto findById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con ID: " + id));
        return serviceMapper.toResponseDto(service);
    }

    public ServiceRequestDto getServiceForEdit(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con ID: " + id));
        return serviceMapper.toRequestDto(service);
    }

    @Transactional
    public void updateService(Long id, ServiceRequestDto dto) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con ID: " + id));

        serviceRepository.findByServiceCode(dto.serviceCode())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new BusinessRuleException("El código de servicio ya está registrado en el sistema.");
                });

        serviceRepository.findByName(dto.name())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new BusinessRuleException("El nombre del servicio ya está en uso por otro servicio.");
                });

        serviceMapper.updateEntityFromDto(dto, service);
        serviceRepository.save(service);
    }

    @Transactional
    public void activateService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con ID: " + id));
        service.activate();
        serviceRepository.save(service);
    }

    @Transactional
    public void deactivateService(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Servicio no encontrado con ID: " + id));
        service.deactivate();
        serviceRepository.save(service);
    }

    public boolean existsById(Long id) {
        return serviceRepository.existsById(id);
    }
}
