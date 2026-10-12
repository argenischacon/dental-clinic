package com.argenischacon.dentalclinic.service;

import com.argenischacon.dentalclinic.dto.treatment.*;
import com.argenischacon.dentalclinic.mappers.TreatmentMapper;
import com.argenischacon.dentalclinic.model.Treatment;
import com.argenischacon.dentalclinic.repository.TreatmentRepository;
import com.argenischacon.dentalclinic.specification.TreatmentSpecification;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;
    private final TreatmentMapper treatmentMapper;

    @Transactional
    public void treatmentAdd(TreatmentRequestDto dto) {

        if (treatmentRepository.findByTreatmentCode(dto.treatmentCode()).isPresent()) {
            throw new BusinessRuleException("El código de tratamiento ya está registrado en el sistema");
        }

        if (treatmentRepository.findByName(dto.name()).isPresent()) {
            throw new BusinessRuleException("El nombre del tratamiento ya está en uso");
        }

        Treatment treatment = treatmentMapper.toEntity(dto);
        treatmentRepository.save(treatment);
    }

    public Page<TreatmentListDto> findAllTreatments(String search, Boolean active, Pageable pageable) {
        Specification<Treatment> spec = TreatmentSpecification.build(search, active);
        return treatmentRepository.findAll(spec, pageable).map(treatmentMapper::toListDto);
    }

    public List<TreatmentNestedDto> findAllActiveTreatments() {
        return treatmentRepository.findAllByActiveTrue().stream()
                .map(treatmentMapper::toNestedDto)
                .toList();
    }

    public TreatmentStatsDto getStats() {
        long total = treatmentRepository.count();
        long active = treatmentRepository.countByActiveTrue();
        long inactive = treatmentRepository.countByActiveFalse();
        return new TreatmentStatsDto(total, active, inactive);
    }

    public TreatmentResponseDto findById(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado con ID: " + id));
        return treatmentMapper.toResponseDto(treatment);
    }

    public TreatmentRequestDto getTreatmentForEdit(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado con ID: " + id));
        return treatmentMapper.toRequestDto(treatment);
    }

    @Transactional
    public void updateTreatment(Long id, TreatmentRequestDto dto) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado con ID: " + id));

        treatmentRepository.findByTreatmentCode(dto.treatmentCode())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new BusinessRuleException("El código de tratamiento ya está registrado en el sistema.");
                });

        treatmentRepository.findByName(dto.name())
                .filter(s -> !s.getId().equals(id))
                .ifPresent(s -> {
                    throw new BusinessRuleException("El nombre del tratamiento ya está en uso por otro tratamiento.");
                });

        treatmentMapper.updateEntityFromDto(dto, treatment);
        treatmentRepository.save(treatment);
    }

    @Transactional
    public void activateTreatment(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado con ID: " + id));
        treatment.activate();
        treatmentRepository.save(treatment);
    }

    @Transactional
    public void deactivateTreatment(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tratamiento no encontrado con ID: " + id));
        treatment.deactivate();
        treatmentRepository.save(treatment);
    }

    public boolean existsById(Long id) {
        return treatmentRepository.existsById(id);
    }
}
