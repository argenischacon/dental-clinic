package com.argenischacon.dentalclinic.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "work_schedules", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"dentist_id", "day_of_week"})
})
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Builder.Default
    private int slotDurationMinutes = 30;

    @Builder.Default
    private boolean available = true;

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    @Builder.Default
    @OneToMany(mappedBy = "workSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleBreak> breaks = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isWithin(LocalTime start, LocalTime end) {
        return !start.isBefore(this.startTime) && !end.isAfter(this.endTime);
    }

    public boolean isInBreak(LocalTime start, LocalTime end) {
        if (breaks == null || breaks.isEmpty()) return false;
        for (ScheduleBreak b : breaks) {
            if (b.overlapsSlot(start, end)) {
                return true;
            }
        }
        return false;
    }

    public List<TimeSlot> getAvailableSlots(int durationMinutes) {
        List<TimeSlot> slots = new ArrayList<>();

        LocalTime candidateStart = this.startTime;

        while (!candidateStart.plusMinutes(durationMinutes).isAfter(this.endTime)) {
            LocalTime candidateEnd = candidateStart.plusMinutes(durationMinutes);

            if (!isInBreak(candidateStart, candidateEnd)) {
                slots.add(new TimeSlot(candidateStart, candidateEnd, true));
            }

            candidateStart = candidateStart.plusMinutes(slotDurationMinutes);
        }

        return slots;
    }

    public int getTotalWorkMinutes() {
        int totalMinutes = (int) Duration.between(startTime, endTime).toMinutes();
        
        if (breaks != null) {
            for (ScheduleBreak b : breaks) {
                totalMinutes -= (int) Duration.between(b.getStartBreak(), b.getEndBreak()).toMinutes();
            }
        }
        
        return totalMinutes;
    }

    public void addBreak(ScheduleBreak scheduleBreak) {
        breaks.add(scheduleBreak);
        scheduleBreak.setWorkSchedule(this);
    }

    public void removeBreak(ScheduleBreak scheduleBreak) {
        breaks.remove(scheduleBreak);
        scheduleBreak.setWorkSchedule(null);
    }
}
