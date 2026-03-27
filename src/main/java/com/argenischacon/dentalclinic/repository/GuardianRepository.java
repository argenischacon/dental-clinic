package com.argenischacon.dentalclinic.repository;

import com.argenischacon.dentalclinic.model.Guardian;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long>, JpaSpecificationExecutor<Guardian> {
    Optional<Guardian> findByDni(String dni);
    Optional<Guardian> findByEmail(String email);
    Page<Guardian> findAllByActiveTrue(Pageable pageable);
    Optional<Guardian> findByIdAndActiveTrue(Long id);
}
