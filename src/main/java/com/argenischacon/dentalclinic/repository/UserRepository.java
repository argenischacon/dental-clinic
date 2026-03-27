package com.argenischacon.dentalclinic.repository;

import com.argenischacon.dentalclinic.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findByUsername(String username);
    Page<User> findAllByActiveTrue(Pageable pageable);
    Optional<User> findByIdAndActiveTrue(Long id);
    boolean existsByRole(com.argenischacon.dentalclinic.enums.Role role);
}
