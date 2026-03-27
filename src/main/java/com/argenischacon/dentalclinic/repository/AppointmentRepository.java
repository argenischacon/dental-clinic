package com.argenischacon.dentalclinic.repository;

import com.argenischacon.dentalclinic.enums.AppointmentStatus;
import com.argenischacon.dentalclinic.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {
    Optional<Appointment> findByAppointmentCode(String appointmentCode);
    Page<Appointment> findAllByActiveTrue(Pageable pageable);
    Optional<Appointment> findByIdAndActiveTrue(Long id);
    Page<Appointment> findAllByActiveFalse(Pageable pageable);
    List<Appointment> findByDentistIdAndDateAndActiveTrueAndStatusIn(Long dentistId, LocalDate date, List<AppointmentStatus> statuses);
    List<Appointment> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
