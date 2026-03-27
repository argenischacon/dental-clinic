package com.argenischacon.dentalclinic.repository;

import com.argenischacon.dentalclinic.model.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {
    Optional<Service> findByServiceCode(String serviceCode);
    Optional<Service> findByName(String name);
    Page<Service> findAllByActiveTrue(Pageable pageable);
    Optional<Service> findByIdAndActiveTrue(Long id);
}
