package com.argenischacon.dentalclinic.repository;

import com.argenischacon.dentalclinic.model.WorkSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.Optional;

@Repository
public interface WorkScheduleRepository extends JpaRepository<WorkSchedule, Long> {
    Optional<WorkSchedule> findByDentistIdAndDayOfWeek(Long dentistId, DayOfWeek dayOfWeek);
}
