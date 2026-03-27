package com.argenischacon.dentalclinic.repository;

import com.argenischacon.dentalclinic.model.Receptionist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist, Long>, JpaSpecificationExecutor<Receptionist> {
    Optional<Receptionist> findByEmployeeNumber(String employeeNumber);
    Optional<Receptionist> findByDni(String dni);
    Optional<Receptionist> findByEmail(String email);
    Page<Receptionist> findAllByActiveTrue(Pageable pageable);
    Optional<Receptionist> findByIdAndActiveTrue(Long id);
}
