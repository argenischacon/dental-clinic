package com.argenischacon.dentalclinic.repository;

import com.argenischacon.dentalclinic.model.Treatment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TreatmentRepository extends JpaRepository<Treatment, Long>, JpaSpecificationExecutor<Treatment> {
    Optional<Treatment> findByTreatmentCode(String serviceCode);
    Optional<Treatment> findByName(String name);
    Page<Treatment> findAllByActiveTrue(Pageable pageable);
    List<Treatment> findAllByActiveTrue();
    Optional<Treatment> findByIdAndActiveTrue(Long id);
    
    long countByActiveTrue();
    long countByActiveFalse();
}
