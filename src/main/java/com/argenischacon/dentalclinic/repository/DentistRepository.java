package com.argenischacon.dentalclinic.repository;

import com.argenischacon.dentalclinic.model.Dentist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DentistRepository extends JpaRepository<Dentist, Long>, JpaSpecificationExecutor<Dentist> {
    Optional<Dentist> findByLicenseNumber(String licenseNumber);
    Optional<Dentist> findByDni(String dni);
    Optional<Dentist> findByEmail(String email);
    Page<Dentist> findAllByActiveTrue(Pageable pageable);
    Optional<Dentist> findByIdAndActiveTrue(Long id);
    Page<Dentist> findAllByActiveFalse(Pageable pageable);
}
