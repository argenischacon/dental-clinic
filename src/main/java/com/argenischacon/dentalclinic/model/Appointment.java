package com.argenischacon.dentalclinic.model;

import static com.argenischacon.dentalclinic.enums.AppointmentStatus.*;

import com.argenischacon.dentalclinic.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String appointmentCode;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = SCHEDULED;

    @Column(length = 500)
    private String notes;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private Receptionist createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isCancellable() {
        return status != COMPLETED && status != CANCELLED && status != NO_SHOW;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void cancel() {
        if (status == COMPLETED || status == CANCELLED || status == NO_SHOW)
            throw new IllegalStateException("No se puede cancelar un turno en estado: " + status);
        this.status = CANCELLED;
    }

    public void confirm() {
        if (status != SCHEDULED)
            throw new IllegalStateException("No se puede confirmar un turno en estado: " + status);
        this.status = CONFIRMED;
    }

    public void markInProgress() {
        if (status != SCHEDULED && status != CONFIRMED)
            throw new IllegalStateException("No se puede iniciar un turno en estado: " + status);
        this.status = IN_PROGRESS;
    }

    public void complete() {
        if (status == CANCELLED || status == NO_SHOW)
            throw new IllegalStateException("No se puede completar un turno en estado: " + status);
        this.status = COMPLETED;
    }

    public void markNoShow() {
        if (status != SCHEDULED && status != CONFIRMED)
            throw new IllegalStateException("No se puede marcar NO_SHOW un turno en estado: " + status);
        this.status = NO_SHOW;
    }
}
