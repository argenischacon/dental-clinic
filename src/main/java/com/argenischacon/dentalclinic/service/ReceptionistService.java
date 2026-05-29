package com.argenischacon.dentalclinic.service;

import com.argenischacon.dentalclinic.dto.receptionist.*;
import com.argenischacon.dentalclinic.enums.Role;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.mappers.ReceptionistMapper;
import com.argenischacon.dentalclinic.model.Receptionist;
import com.argenischacon.dentalclinic.model.User;
import com.argenischacon.dentalclinic.repository.ReceptionistRepository;
import com.argenischacon.dentalclinic.specification.ReceptionistSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceptionistService {

    private final ReceptionistRepository receptionistRepository;
    private final ReceptionistMapper receptionistMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void receptionistAdd(ReceptionistRequestDto dto) {

        if (receptionistRepository.findByDni(dto.dni()).isPresent()) {
            throw new BusinessRuleException("El DNI ya está registrado en el sistema");
        }

        if (receptionistRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessRuleException("El email ya está en uso");
        }

        if (receptionistRepository.findByEmployeeNumber(dto.employeeNumber()).isPresent()) {
            throw new BusinessRuleException("El número de empleado ya está registrado en el sistema");
        }

        Receptionist receptionist = receptionistMapper.toEntity(dto);

        User newUser = User.builder()
                .username(receptionist.getDni())
                .password(passwordEncoder.encode(receptionist.getDni()))
                .role(Role.RECEPTIONIST)
                .build();

        receptionist.setUser(newUser);
        receptionistRepository.save(receptionist);
    }

    public Page<ReceptionistListDto> findAllReceptionists(String search, Boolean active, Pageable pageable) {
        Specification<Receptionist> spec = ReceptionistSpecification.build(search, active);
        return receptionistRepository.findAll(spec, pageable).map(receptionistMapper::toListDto);
    }

    public List<ReceptionistNestedDto> findAllActiveReceptionists() {
        return receptionistRepository.findAllByActiveTrue().stream()
                .map(receptionistMapper::toNestedDto)
                .toList();
    }

    public ReceptionistStatsDto getStats() {
        long total = receptionistRepository.count();
        long active = receptionistRepository.countByActiveTrue();
        long inactive = receptionistRepository.countByActiveFalse();
        return new ReceptionistStatsDto(total, active, inactive);
    }

    public ReceptionistResponseDto findById(Long id) {
        Receptionist receptionist = receptionistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recepcionista no encontrado/a con ID: " + id));
        return receptionistMapper.toResponseDto(receptionist);
    }

    public ReceptionistRequestDto getReceptionistForEdit(Long id) {
        Receptionist receptionist = receptionistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recepcionista no encontrado/a con ID: " + id));
        return receptionistMapper.toRequestDto(receptionist);
    }

    @Transactional
    public void updateReceptionist(Long id, ReceptionistRequestDto dto) {
        Receptionist receptionist = receptionistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recepcionista no encontrado/a con ID: " + id));

        receptionistRepository.findByEmail(dto.email())
                .filter(r -> !r.getId().equals(id))
                .ifPresent(r -> {
                    throw new BusinessRuleException("El email ya está en uso por otro/a recepcionista.");
                });

        receptionistRepository.findByEmployeeNumber(dto.employeeNumber())
                .filter(r -> !r.getId().equals(id))
                .ifPresent(r -> {
                    throw new BusinessRuleException("El número de empleado ya está registrado en el sistema");
                });

        boolean dniChanged = !receptionist.getDni().equals(dto.dni());
        if (dniChanged) {
            receptionistRepository.findByDni(dto.dni())
                    .ifPresent(r -> {
                        throw new BusinessRuleException("El DNI ya está registrado en el sistema.");
                    });

            receptionist.getUser().setUsername(dto.dni());

            if (receptionist.getUser().isMustChangePassword()) {
                receptionist.getUser().setPassword(passwordEncoder.encode(dto.dni()));
            }
        }

        receptionistMapper.updateEntityFromDto(dto, receptionist);
        receptionistRepository.save(receptionist);
    }

    @Transactional
    public void activateReceptionist(Long id) {
        Receptionist receptionist = receptionistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recepcionista no encontrado/a con ID: " + id));
        receptionist.activate();
        receptionistRepository.save(receptionist);
    }

    @Transactional
    public void deactivateReceptionist(Long id) {
        Receptionist receptionist = receptionistRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recepcionista no encontrado/a con ID: " + id));
        receptionist.deactivate();
        receptionistRepository.save(receptionist);
    }

    public boolean existsById(Long id) {
        return receptionistRepository.existsById(id);
    }
}
