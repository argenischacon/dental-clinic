package com.argenischacon.dentalclinic.repository;

import com.argenischacon.dentalclinic.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    Optional<Patient> findByPatientCode(String patientCode);
    Optional<Patient> findByDni(String dni);
    Optional<Patient> findByEmail(String email);
    Page<Patient> findAllByActiveTrue(Pageable pageable);
    Optional<Patient> findByIdAndActiveTrue(Long id);
    Page<Patient> findAllByActiveFalse(Pageable pageable);
}
