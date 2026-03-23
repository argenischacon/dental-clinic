package com.argenischacon.dentalclinic.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "schedule_breaks")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleBreak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalTime startBreak;

    @Column(nullable = false)
    private LocalTime endBreak;

    @Column(nullable = false, length = 50)
    private String label;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_schedule_id", nullable = false)
    private WorkSchedule workSchedule;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public int getDurationMinutes() {
        return (int) Duration.between(startBreak, endBreak).toMinutes();
    }

    public boolean overlapsSlot(LocalTime start, LocalTime end){
        return start.isBefore(endBreak) && end.isAfter(startBreak);
    }

}
