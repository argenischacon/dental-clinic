package com.argenischacon.dentalclinic.unit.service;

import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistListDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistRequestDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistResponseDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistStatsDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.mappers.ReceptionistMapper;
import com.argenischacon.dentalclinic.model.Receptionist;
import com.argenischacon.dentalclinic.model.User;
import com.argenischacon.dentalclinic.repository.ReceptionistRepository;
import com.argenischacon.dentalclinic.service.ReceptionistService;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReceptionistServiceTest {

    @Mock
    private ReceptionistRepository receptionistRepository;

    @Mock
    private ReceptionistMapper receptionistMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ReceptionistService receptionistService;

    private ReceptionistRequestDto defaultRequestDto;
    private Receptionist defaultReceptionist;
    private User defaultUser;

    @BeforeEach
    void setUp() {
        defaultRequestDto = new ReceptionistRequestDto(
                "12345678", "Maria", "Gomez", "maria.gomez@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "EMP-001", LocalDate.of(2020, 1, 1)
        );

        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setUsername("12345678");
        defaultUser.setMustChangePassword(true);

        defaultReceptionist = new Receptionist();
        defaultReceptionist.setId(1L);
        defaultReceptionist.setName("Maria");
        defaultReceptionist.setLastName("Gomez");
        defaultReceptionist.setDni("12345678");
        defaultReceptionist.setEmployeeNumber("EMP-001");
        defaultReceptionist.setEmail("maria.gomez@example.com");
        defaultReceptionist.setUser(defaultUser);
    }

    @Test
    void testReceptionistAdd_Success() {
        when(receptionistRepository.findByDni(defaultRequestDto.dni())).thenReturn(Optional.empty());
        when(receptionistRepository.findByEmail(defaultRequestDto.email())).thenReturn(Optional.empty());
        when(receptionistRepository.findByEmployeeNumber(defaultRequestDto.employeeNumber())).thenReturn(Optional.empty());
        
        when(receptionistMapper.toEntity(defaultRequestDto)).thenReturn(defaultReceptionist);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        receptionistService.receptionistAdd(defaultRequestDto);

        verify(receptionistRepository, times(1)).save(any(Receptionist.class));
        assertNotNull(defaultReceptionist.getUser());
        assertEquals("12345678", defaultReceptionist.getUser().getUsername());
    }

    @Test
    void testReceptionistAdd_DuplicateDni_ThrowsException() {
        when(receptionistRepository.findByDni(defaultRequestDto.dni())).thenReturn(Optional.of(defaultReceptionist));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> receptionistService.receptionistAdd(defaultRequestDto));
        assertEquals("El DNI ya está registrado en el sistema", exception.getMessage());
        verify(receptionistRepository, never()).save(any());
    }

    @Test
    void testReceptionistAdd_DuplicateEmail_ThrowsException() {
        when(receptionistRepository.findByDni(defaultRequestDto.dni())).thenReturn(Optional.empty());
        when(receptionistRepository.findByEmail(defaultRequestDto.email())).thenReturn(Optional.of(defaultReceptionist));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> receptionistService.receptionistAdd(defaultRequestDto));
        assertEquals("El email ya está en uso", exception.getMessage());
        verify(receptionistRepository, never()).save(any());
    }

    @Test
    void testReceptionistAdd_DuplicateEmployeeNumber_ThrowsException() {
        when(receptionistRepository.findByDni(defaultRequestDto.dni())).thenReturn(Optional.empty());
        when(receptionistRepository.findByEmail(defaultRequestDto.email())).thenReturn(Optional.empty());
        when(receptionistRepository.findByEmployeeNumber(defaultRequestDto.employeeNumber())).thenReturn(Optional.of(defaultReceptionist));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> receptionistService.receptionistAdd(defaultRequestDto));
        assertEquals("El número de empleado ya está registrado en el sistema", exception.getMessage());
        verify(receptionistRepository, never()).save(any());
    }

    @Test
    void testFindById_Success() {
        when(receptionistRepository.findById(1L)).thenReturn(Optional.of(defaultReceptionist));
        ReceptionistResponseDto responseDto = new ReceptionistResponseDto(
                1L, "12345678", "Maria", "Gomez", "maria.gomez@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "EMP-001", LocalDate.of(2020, 1, 1), "12345678", true
        );
        when(receptionistMapper.toResponseDto(defaultReceptionist)).thenReturn(responseDto);

        ReceptionistResponseDto result = receptionistService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void testFindById_NotFound_ThrowsException() {
        when(receptionistRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> receptionistService.findById(1L));
        assertEquals("Recepcionista no encontrado/a con ID: 1", exception.getMessage());
    }

    @Test
    void testGetReceptionistForEdit_Success() {
        when(receptionistRepository.findById(1L)).thenReturn(Optional.of(defaultReceptionist));
        when(receptionistMapper.toRequestDto(defaultReceptionist)).thenReturn(defaultRequestDto);

        ReceptionistRequestDto result = receptionistService.getReceptionistForEdit(1L);
        assertNotNull(result);
        assertEquals(defaultRequestDto.dni(), result.dni());
    }

    @Test
    void testUpdateReceptionist_Success() {
        when(receptionistRepository.findById(1L)).thenReturn(Optional.of(defaultReceptionist));
        when(receptionistRepository.findByEmail(defaultRequestDto.email())).thenReturn(Optional.empty());
        when(receptionistRepository.findByEmployeeNumber(defaultRequestDto.employeeNumber())).thenReturn(Optional.empty());
        
        // DNI NO cambia
        ReceptionistRequestDto requestWithSameDni = new ReceptionistRequestDto(
                "12345678", "Maria Updated", "Gomez", "maria.gomez@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "EMP-001", LocalDate.of(2020, 1, 1)
        );
        
        when(receptionistMapper.updateEntityFromDto(requestWithSameDni, defaultReceptionist)).thenReturn(defaultReceptionist);

        receptionistService.updateReceptionist(1L, requestWithSameDni);

        verify(receptionistRepository, times(1)).save(defaultReceptionist);
        verify(receptionistMapper, times(1)).updateEntityFromDto(requestWithSameDni, defaultReceptionist);
    }

    @Test
    void testUpdateReceptionist_ChangeDni_Success() {
        ReceptionistRequestDto requestWithNewDni = new ReceptionistRequestDto(
                "87654321", "Maria", "Gomez", "maria.gomez@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "EMP-001", LocalDate.of(2020, 1, 1)
        );
        
        when(receptionistRepository.findById(1L)).thenReturn(Optional.of(defaultReceptionist));
        when(receptionistRepository.findByEmail(requestWithNewDni.email())).thenReturn(Optional.empty());
        when(receptionistRepository.findByEmployeeNumber(requestWithNewDni.employeeNumber())).thenReturn(Optional.empty());
        when(receptionistRepository.findByDni(requestWithNewDni.dni())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("87654321")).thenReturn("encodedNewDni");

        receptionistService.updateReceptionist(1L, requestWithNewDni);

        assertEquals("87654321", defaultReceptionist.getUser().getUsername());
        verify(receptionistRepository, times(1)).save(defaultReceptionist);
    }

    @Test
    void testActivateReceptionist_Success() {
        defaultReceptionist.setActive(false);
        when(receptionistRepository.findById(1L)).thenReturn(Optional.of(defaultReceptionist));

        receptionistService.activateReceptionist(1L);

        assertTrue(defaultReceptionist.isActive());
        verify(receptionistRepository, times(1)).save(defaultReceptionist);
    }

    @Test
    void testDeactivateReceptionist_Success() {
        defaultReceptionist.setActive(true);
        when(receptionistRepository.findById(1L)).thenReturn(Optional.of(defaultReceptionist));

        receptionistService.deactivateReceptionist(1L);

        assertFalse(defaultReceptionist.isActive());
        verify(receptionistRepository, times(1)).save(defaultReceptionist);
    }

    @Test
    void testFindAllReceptionists() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Receptionist> page = new PageImpl<>(List.of(defaultReceptionist));
        when(receptionistRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);
        
        ReceptionistListDto listDto = new ReceptionistListDto(
                1L, "EMP-001", "12345678", "Maria", "Gomez",
                "maria.gomez@example.com", "123456789", true
        );
        when(receptionistMapper.toListDto(defaultReceptionist)).thenReturn(listDto);

        Page<ReceptionistListDto> result = receptionistService.findAllReceptionists("Maria", true, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals("Maria", result.getContent().get(0).name());
    }

    @Test
    void testGetStats() {
        when(receptionistRepository.count()).thenReturn(10L);
        when(receptionistRepository.countByActiveTrue()).thenReturn(8L);
        when(receptionistRepository.countByActiveFalse()).thenReturn(2L);

        ReceptionistStatsDto stats = receptionistService.getStats();

        assertEquals(10L, stats.totalReceptionists());
        assertEquals(8L, stats.activeReceptionists());
        assertEquals(2L, stats.inactiveReceptionists());
    }
}
