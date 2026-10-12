package com.argenischacon.dentalclinic.unit.service;

import com.argenischacon.dentalclinic.dto.treatment.*;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.mappers.TreatmentMapper;
import com.argenischacon.dentalclinic.model.Treatment;
import com.argenischacon.dentalclinic.repository.TreatmentRepository;
import com.argenischacon.dentalclinic.service.TreatmentService;
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
public class TreatmentServiceTest {

    @Mock
    private TreatmentRepository treatmentRepository;

    @Mock
    private TreatmentMapper treatmentMapper;

    @InjectMocks
    private TreatmentService treatmentService;

    private TreatmentRequestDto defaultRequestDto;
    private Treatment defaultTreatment;

    @BeforeEach
    void setUp() {
        defaultRequestDto = new TreatmentRequestDto(
                "EXT-001", "Extracción Dental", "Extracción simple de pieza dental", 30, new BigDecimal("50.00")
        );

        defaultTreatment = new Treatment();
        defaultTreatment.setId(1L);
        defaultTreatment.setTreatmentCode("EXT-001");
        defaultTreatment.setName("Extracción Dental");
        defaultTreatment.setDescription("Extracción simple de pieza dental");
        defaultTreatment.setDurationMinutes(30);
        defaultTreatment.setPrice(new BigDecimal("50.00"));
        defaultTreatment.setActive(true);
    }

    @Test
    void testTreatmentAdd_Success() {
        when(treatmentRepository.findByTreatmentCode(defaultRequestDto.treatmentCode())).thenReturn(Optional.empty());
        when(treatmentRepository.findByName(defaultRequestDto.name())).thenReturn(Optional.empty());
        when(treatmentMapper.toEntity(defaultRequestDto)).thenReturn(defaultTreatment);

        treatmentService.treatmentAdd(defaultRequestDto);

        verify(treatmentRepository, times(1)).save(defaultTreatment);
    }

    @Test
    void testTreatmentAdd_DuplicateCode_ThrowsException() {
        when(treatmentRepository.findByTreatmentCode(defaultRequestDto.treatmentCode())).thenReturn(Optional.of(defaultTreatment));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> treatmentService.treatmentAdd(defaultRequestDto));
        assertEquals("El código de tratamiento ya está registrado en el sistema", exception.getMessage());
        verify(treatmentRepository, never()).save(any());
    }

    @Test
    void testTreatmentAdd_DuplicateName_ThrowsException() {
        when(treatmentRepository.findByTreatmentCode(defaultRequestDto.treatmentCode())).thenReturn(Optional.empty());
        when(treatmentRepository.findByName(defaultRequestDto.name())).thenReturn(Optional.of(defaultTreatment));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> treatmentService.treatmentAdd(defaultRequestDto));
        assertEquals("El nombre del tratamiento ya está en uso", exception.getMessage());
        verify(treatmentRepository, never()).save(any());
    }

    @Test
    void testFindById_Success() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(defaultTreatment));
        TreatmentResponseDto responseDto = new TreatmentResponseDto(
                1L, "EXT-001", "Extracción Dental", "Descripción", 30, new BigDecimal("50.00"), true, LocalDateTime.now(), LocalDateTime.now()
        );
        when(treatmentMapper.toResponseDto(defaultTreatment)).thenReturn(responseDto);

        TreatmentResponseDto result = treatmentService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("EXT-001", result.treatmentCode());
    }

    @Test
    void testFindById_NotFound_ThrowsException() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> treatmentService.findById(1L));
        assertEquals("Tratamiento no encontrado con ID: 1", exception.getMessage());
    }

    @Test
    void testGetTreatmentForEdit_Success() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(defaultTreatment));
        when(treatmentMapper.toRequestDto(defaultTreatment)).thenReturn(defaultRequestDto);

        TreatmentRequestDto result = treatmentService.getTreatmentForEdit(1L);
        assertNotNull(result);
        assertEquals(defaultRequestDto.treatmentCode(), result.treatmentCode());
    }

    @Test
    void testUpdateTreatment_Success() {
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(defaultTreatment));
        
        // Simular que no hay conflictos de unicidad con otros tratamientos
        when(treatmentRepository.findByTreatmentCode(defaultRequestDto.treatmentCode())).thenReturn(Optional.empty());
        when(treatmentRepository.findByName(defaultRequestDto.name())).thenReturn(Optional.empty());

        treatmentService.updateTreatment(1L, defaultRequestDto);

        verify(treatmentMapper, times(1)).updateEntityFromDto(defaultRequestDto, defaultTreatment);
        verify(treatmentRepository, times(1)).save(defaultTreatment);
    }

    @Test
    void testUpdateTreatment_ChangeName_ThrowsExceptionIfDuplicate() {
        Treatment otherTreatment = new Treatment();
        otherTreatment.setId(2L); // ID diferente al tratamiento que se está editando
        otherTreatment.setName("Extracción Dental Modificada");
        
        TreatmentRequestDto updatedRequest = new TreatmentRequestDto(
                "EXT-001", "Extracción Dental Modificada", "Nueva desc", 30, new BigDecimal("50.00")
        );

        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(defaultTreatment));
        when(treatmentRepository.findByTreatmentCode(updatedRequest.treatmentCode())).thenReturn(Optional.empty());
        // El nuevo nombre ya lo tiene el tratamiento con ID 2
        when(treatmentRepository.findByName(updatedRequest.name())).thenReturn(Optional.of(otherTreatment));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> treatmentService.updateTreatment(1L, updatedRequest));
        assertEquals("El nombre del tratamiento ya está en uso por otro tratamiento.", exception.getMessage());
        
        verify(treatmentRepository, never()).save(any());
    }

    @Test
    void testActivateTreatment_Success() {
        defaultTreatment.setActive(false);
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(defaultTreatment));

        treatmentService.activateTreatment(1L);

        assertTrue(defaultTreatment.isActive());
        verify(treatmentRepository, times(1)).save(defaultTreatment);
    }

    @Test
    void testDeactivateTreatment_Success() {
        defaultTreatment.setActive(true);
        when(treatmentRepository.findById(1L)).thenReturn(Optional.of(defaultTreatment));

        treatmentService.deactivateTreatment(1L);

        assertFalse(defaultTreatment.isActive());
        verify(treatmentRepository, times(1)).save(defaultTreatment);
    }

    @Test
    void testFindAllTreatments() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Treatment> page = new PageImpl<>(List.of(defaultTreatment));
        when(treatmentRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);
        
        TreatmentListDto listDto = new TreatmentListDto(
                1L, "EXT-001", "Extracción Dental", new BigDecimal("50.00"), 30, true
        );
        when(treatmentMapper.toListDto(defaultTreatment)).thenReturn(listDto);

        Page<TreatmentListDto> result = treatmentService.findAllTreatments("Extracción", true, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals("Extracción Dental", result.getContent().get(0).name());
    }

    @Test
    void testGetStats() {
        when(treatmentRepository.count()).thenReturn(15L);
        when(treatmentRepository.countByActiveTrue()).thenReturn(10L);
        when(treatmentRepository.countByActiveFalse()).thenReturn(5L);

        TreatmentStatsDto stats = treatmentService.getStats();

        assertEquals(15L, stats.totalTreatments());
        assertEquals(10L, stats.activeTreatments());
        assertEquals(5L, stats.inactiveTreatments());
    }
}
