package com.argenischacon.dentalclinic.unit.service;

import com.argenischacon.dentalclinic.dto.dentist.DentistListDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistRequestDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistResponseDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistStatsDto;
import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.mappers.DentistMapper;
import com.argenischacon.dentalclinic.model.Dentist;
import com.argenischacon.dentalclinic.model.User;
import com.argenischacon.dentalclinic.repository.DentistRepository;
import com.argenischacon.dentalclinic.service.DentistService;
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
public class DentistServiceTest {

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private DentistMapper dentistMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DentistService dentistService;

    private DentistRequestDto defaultRequestDto;
    private Dentist defaultDentist;
    private User defaultUser;

    @BeforeEach
    void setUp() {
        defaultRequestDto = new DentistRequestDto(
                "12345678", "John", "Doe", "john.doe@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "LIC-123",
                DentalSpecialty.GENERAL, LocalDate.of(2020, 1, 1)
        );

        defaultUser = new User();
        defaultUser.setId(1L);
        defaultUser.setUsername("12345678");
        defaultUser.setMustChangePassword(true);

        defaultDentist = new Dentist();
        defaultDentist.setId(1L);
        defaultDentist.setName("John");
        defaultDentist.setLastName("Doe");
        defaultDentist.setDni("12345678");
        defaultDentist.setLicenseNumber("LIC-123");
        defaultDentist.setEmail("john.doe@example.com");
        defaultDentist.setUser(defaultUser);
    }

    @Test
    void testDentistAdd_Success() {
        when(dentistRepository.findByDni(defaultRequestDto.dni())).thenReturn(Optional.empty());
        when(dentistRepository.findByEmail(defaultRequestDto.email())).thenReturn(Optional.empty());
        when(dentistRepository.findByLicenseNumber(defaultRequestDto.licenseNumber())).thenReturn(Optional.empty());
        
        when(dentistMapper.toEntity(defaultRequestDto)).thenReturn(defaultDentist);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        dentistService.dentistAdd(defaultRequestDto);

        verify(dentistRepository, times(1)).save(any(Dentist.class));
        assertNotNull(defaultDentist.getUser());
        assertEquals("12345678", defaultDentist.getUser().getUsername());
    }

    @Test
    void testDentistAdd_DuplicateDni_ThrowsException() {
        when(dentistRepository.findByDni(defaultRequestDto.dni())).thenReturn(Optional.of(defaultDentist));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> dentistService.dentistAdd(defaultRequestDto));
        assertEquals("El DNI ya está registrado en el sistema", exception.getMessage());
        verify(dentistRepository, never()).save(any());
    }

    @Test
    void testDentistAdd_DuplicateEmail_ThrowsException() {
        when(dentistRepository.findByDni(defaultRequestDto.dni())).thenReturn(Optional.empty());
        when(dentistRepository.findByEmail(defaultRequestDto.email())).thenReturn(Optional.of(defaultDentist));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> dentistService.dentistAdd(defaultRequestDto));
        assertEquals("El email ya está en uso", exception.getMessage());
        verify(dentistRepository, never()).save(any());
    }

    @Test
    void testDentistAdd_DuplicateLicense_ThrowsException() {
        when(dentistRepository.findByDni(defaultRequestDto.dni())).thenReturn(Optional.empty());
        when(dentistRepository.findByEmail(defaultRequestDto.email())).thenReturn(Optional.empty());
        when(dentistRepository.findByLicenseNumber(defaultRequestDto.licenseNumber())).thenReturn(Optional.of(defaultDentist));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> dentistService.dentistAdd(defaultRequestDto));
        assertEquals("El número de licencia ya está registrado en el sistema", exception.getMessage());
        verify(dentistRepository, never()).save(any());
    }

    @Test
    void testFindById_Success() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(defaultDentist));
        DentistResponseDto responseDto = new DentistResponseDto(
                1L, "12345678", "John", "Doe", "john@example.com", "123456789",
                "Av. Siempreviva 123", "12345678", LocalDate.of(1990, 1, 1),
                "LIC-123", DentalSpecialty.GENERAL, LocalDate.of(2020, 1, 1), true
        );
        when(dentistMapper.toResponseDto(defaultDentist)).thenReturn(responseDto);

        DentistResponseDto result = dentistService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void testFindById_NotFound_ThrowsException() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> dentistService.findById(1L));
        assertEquals("Odontólogo no encontrado con ID: 1", exception.getMessage());
    }

    @Test
    void testGetDentistForEdit_Success() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(defaultDentist));
        when(dentistMapper.toRequestDto(defaultDentist)).thenReturn(defaultRequestDto);

        DentistRequestDto result = dentistService.getDentistForEdit(1L);
        assertNotNull(result);
        assertEquals(defaultRequestDto.dni(), result.dni());
    }

    @Test
    void testUpdateDentist_Success() {
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(defaultDentist));
        when(dentistRepository.findByEmail(defaultRequestDto.email())).thenReturn(Optional.empty());
        when(dentistRepository.findByLicenseNumber(defaultRequestDto.licenseNumber())).thenReturn(Optional.empty());
        
        // DNI NO cambia
        DentistRequestDto requestWithSameDni = new DentistRequestDto(
                "12345678", "John Updated", "Doe", "john.doe@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "LIC-123",
                DentalSpecialty.GENERAL, LocalDate.of(2020, 1, 1)
        );
        
        when(dentistMapper.updateEntityFromDto(requestWithSameDni, defaultDentist)).thenReturn(defaultDentist);

        dentistService.updateDentist(1L, requestWithSameDni);

        verify(dentistRepository, times(1)).save(defaultDentist);
        verify(dentistMapper, times(1)).updateEntityFromDto(requestWithSameDni, defaultDentist);
    }

    @Test
    void testUpdateDentist_ChangeDni_Success() {
        DentistRequestDto requestWithNewDni = new DentistRequestDto(
                "87654321", "John", "Doe", "john.doe@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "LIC-123",
                DentalSpecialty.GENERAL, LocalDate.of(2020, 1, 1)
        );
        
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(defaultDentist));
        when(dentistRepository.findByEmail(requestWithNewDni.email())).thenReturn(Optional.empty());
        when(dentistRepository.findByLicenseNumber(requestWithNewDni.licenseNumber())).thenReturn(Optional.empty());
        when(dentistRepository.findByDni(requestWithNewDni.dni())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("87654321")).thenReturn("encodedNewDni");

        dentistService.updateDentist(1L, requestWithNewDni);

        assertEquals("87654321", defaultDentist.getUser().getUsername());
        verify(dentistRepository, times(1)).save(defaultDentist);
    }

    @Test
    void testActivateDentist_Success() {
        defaultDentist.setActive(false);
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(defaultDentist));

        dentistService.activateDentist(1L);

        assertTrue(defaultDentist.isActive());
        verify(dentistRepository, times(1)).save(defaultDentist);
    }

    @Test
    void testDeactivateDentist_Success() {
        defaultDentist.setActive(true);
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(defaultDentist));

        dentistService.deactivateDentist(1L);

        assertFalse(defaultDentist.isActive());
        verify(dentistRepository, times(1)).save(defaultDentist);
    }

    @Test
    void testFindAllDentists() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Dentist> page = new PageImpl<>(List.of(defaultDentist));
        when(dentistRepository.findAll(any(Specification.class), eq(pageRequest))).thenReturn(page);
        
        DentistListDto listDto = new DentistListDto(
                1L, "John", "Doe", "LIC-123", DentalSpecialty.GENERAL,
                "john@example.com", "123456789", true
        );
        when(dentistMapper.toListDto(defaultDentist)).thenReturn(listDto);

        Page<DentistListDto> result = dentistService.findAllDentists("John", "GENERAL", true, pageRequest);

        assertEquals(1, result.getTotalElements());
        assertEquals("John", result.getContent().get(0).name());
    }

    @Test
    void testGetStats() {
        when(dentistRepository.count()).thenReturn(10L);
        when(dentistRepository.countByActiveTrue()).thenReturn(8L);
        when(dentistRepository.countByActiveFalse()).thenReturn(2L);

        DentistStatsDto stats = dentistService.getStats();

        assertEquals(10L, stats.totalDentists());
        assertEquals(8L, stats.activeDentists());
        assertEquals(2L, stats.inactiveDentists());
    }
}
