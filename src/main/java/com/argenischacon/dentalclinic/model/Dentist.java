package com.argenischacon.dentalclinic.model;

import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dentists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Dentist extends Person implements UserOwner {
    @Column(unique = true, nullable = false, length = 20)
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DentalSpecialty specialty;

    @Column(nullable = false)
    private LocalDate hireDate;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true, updatable = false)
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "dentist", fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<WorkSchedule> workSchedules = new ArrayList<>();

    @Override
    public void activate() {
        this.active = true;
        if (this.user != null) {
            this.user.setActive(true);
        }
    }

    @Override
    public void deactivate() {
        this.active = false;
        if (this.user != null) {
            this.user.setActive(false);
        }
        
        if (this.workSchedules != null) {
            this.workSchedules.forEach(ws -> ws.setAvailable(false));
        }
    }

    public WorkSchedule getScheduleForDay(DayOfWeek day) {
        return workSchedules.stream()
                .filter(ws -> ws.getDayOfWeek() == day)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "El dentista no tiene horario para el dia: " + day
                ));
    }

    public void addWorkSchedule(WorkSchedule workSchedule) {
        workSchedules.add(workSchedule);
        workSchedule.setDentist(this);
    }

    public void removeWorkSchedule(WorkSchedule workSchedule) {
        workSchedules.remove(workSchedule);
        workSchedule.setDentist(null);
    }
}
