package com.argenischacon.dentalclinic.unit.service;

import com.argenischacon.dentalclinic.dto.service.ServiceListDto;
import com.argenischacon.dentalclinic.dto.service.ServiceRequestDto;
import com.argenischacon.dentalclinic.dto.service.ServiceResponseDto;
import com.argenischacon.dentalclinic.dto.service.ServiceStatsDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.mappers.ServiceMapper;
import com.argenischacon.dentalclinic.model.Service;
import com.argenischacon.dentalclinic.repository.ServiceRepository;
import com.argenischacon.dentalclinic.service.ServiceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private ServiceService serviceService;

    private ServiceRequestDto defaultRequestDto;
    private Service defaultService;

    @BeforeEach
    void setUp() {
        defaultRequestDto = new ServiceRequestDto(
                "EXT-001", "Extracción Dental", "Extracción simple de pieza dental", 30, new BigDecimal("50.00")
        );

        defaultService = new Service();
        defaultService.setId(1L);
        defaultService.setServiceCode("EXT-001");
        defaultService.setName("Extracción Dental");
        defaultService.setDescription("Extracción simple de pieza dental");
        defaultService.setDurationMinutes(30);
        defaultService.setPrice(new BigDecimal("50.00"));
        defaultService.setActive(true);
    }

    @Test
    void testServiceAdd_Success() {
        when(serviceRepository.findByServiceCode(defaultRequestDto.serviceCode())).thenReturn(Optional.empty());
        when(serviceRepository.findByName(defaultRequestDto.name())).thenReturn(Optional.empty());
        when(serviceMapper.toEntity(defaultRequestDto)).thenReturn(defaultService);

        serviceService.serviceAdd(defaultRequestDto);

        verify(serviceRepository, times(1)).save(defaultService);
    }

    @Test
    void testServiceAdd_DuplicateCode_ThrowsException() {
        when(serviceRepository.findByServiceCode(defaultRequestDto.serviceCode())).thenReturn(Optional.of(defaultService));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> serviceService.serviceAdd(defaultRequestDto));
        assertEquals("El código de servicio ya está registrado en el sistema", exception.getMessage());
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void testServiceAdd_DuplicateName_ThrowsException() {
        when(serviceRepository.findByServiceCode(defaultRequestDto.serviceCode())).thenReturn(Optional.empty());
        when(serviceRepository.findByName(defaultRequestDto.name())).thenReturn(Optional.of(defaultService));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> serviceService.serviceAdd(defaultRequestDto));
        assertEquals("El nombre del servicio ya está en uso", exception.getMessage());
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void testFindById_Success() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(defaultService));
        ServiceResponseDto responseDto = new ServiceResponseDto(
                1L, "EXT-001", "Extracción Dental", "Descripción", 30, new BigDecimal("50.00"), true, LocalDateTime.now(), LocalDateTime.now()
        );
        when(serviceMapper.toResponseDto(defaultService)).thenReturn(responseDto);

        ServiceResponseDto result = serviceService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("EXT-001", result.serviceCode());
    }

    @Test
    void testFindById_NotFound_ThrowsException() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> serviceService.findById(1L));
        assertEquals("Servicio no encontrado con ID: 1", exception.getMessage());
    }

    @Test
    void testGetServiceForEdit_Success() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(defaultService));
        when(serviceMapper.toRequestDto(defaultService)).thenReturn(defaultRequestDto);

        ServiceRequestDto result = serviceService.getServiceForEdit(1L);
        assertNotNull(result);
        assertEquals(defaultRequestDto.serviceCode(), result.serviceCode());
    }

    @Test
    void testUpdateService_Success() {
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(defaultService));
        
        // Simular que no hay conflictos de unicidad con otros servicios
        when(serviceRepository.findByServiceCode(defaultRequestDto.serviceCode())).thenReturn(Optional.empty());
        when(serviceRepository.findByName(defaultRequestDto.name())).thenReturn(Optional.empty());

        serviceService.updateService(1L, defaultRequestDto);

        verify(serviceMapper, times(1)).updateEntityFromDto(defaultRequestDto, defaultService);
        verify(serviceRepository, times(1)).save(defaultService);
    }

    @Test
    void testUpdateService_ChangeName_ThrowsExceptionIfDuplicate() {
        Service otherService = new Service();
        otherService.setId(2L); // ID diferente al servicio que se está editando
        otherService.setName("Extracción Dental Modificada");
        
        ServiceRequestDto updatedRequest = new ServiceRequestDto(
                "EXT-001", "Extracción Dental Modificada", "Nueva desc", 30, new BigDecimal("50.00")
        );

        when(serviceRepository.findById(1L)).thenReturn(Optional.of(defaultService));
        when(serviceRepository.findByServiceCode(updatedRequest.serviceCode())).thenReturn(Optional.empty());
        // El nuevo nombre ya lo tiene el servicio con ID 2
        when(serviceRepository.findByName(updatedRequest.name())).thenReturn(Optional.of(otherService));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> serviceService.updateService(1L, updatedRequest));
        assertEquals("El nombre del servicio ya está en uso por otro servicio.", exception.getMessage());
        
        verify(serviceRepository, never()).save(any());
    }

    @Test
    void testActivateService_Success() {
        defaultService.setActive(false);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(defaultService));

        serviceService.activateService(1L);

        assertTrue(defaultService.isActive());
        verify(serviceRepository, times(1)).save(defaultService);
    }

    @Test
    void testDeactivateService_Success() {
        defaultService.setActive(true);
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(defaultService));

        serviceService.deactivateService(1L);

        assertFalse(defaultService.isActive());
        verify(serviceRepository, times(1)).save(defaultService);
    }

    @Test
    void testFindAllServices() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Service> page = new PageImpl<>(List.of(defaultService));
        when(serviceRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);
        
        ServiceListDto listDto = new ServiceListDto(
                1L, "EXT-001", "Extracción Dental", new BigDecimal("50.00"), 30, true
        );
        when(serviceMapper.toListDto(defaultService)).thenReturn(listDto);

        Page<ServiceListDto> result = serviceService.findAllServices("Extracción", true, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals("Extracción Dental", result.getContent().get(0).name());
    }

    @Test
    void testGetStats() {
        when(serviceRepository.count()).thenReturn(15L);
        when(serviceRepository.countByActiveTrue()).thenReturn(10L);
        when(serviceRepository.countByActiveFalse()).thenReturn(5L);

        ServiceStatsDto stats = serviceService.getStats();

        assertEquals(15L, stats.totalServices());
        assertEquals(10L, stats.activeServices());
        assertEquals(5L, stats.inactiveServices());
    }
}
