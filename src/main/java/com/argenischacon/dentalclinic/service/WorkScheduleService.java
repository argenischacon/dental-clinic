package com.argenischacon.dentalclinic.service;

import com.argenischacon.dentalclinic.dto.schedule.AssignScheduleFormDto;
import com.argenischacon.dentalclinic.dto.schedule.WorkScheduleResponseDto;
import com.argenischacon.dentalclinic.mappers.WorkScheduleMapper;
import com.argenischacon.dentalclinic.model.Dentist;
import com.argenischacon.dentalclinic.model.WorkSchedule;
import com.argenischacon.dentalclinic.repository.DentistRepository;
import com.argenischacon.dentalclinic.repository.WorkScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.argenischacon.dentalclinic.dto.schedule.DailyScheduleFormDto;
import com.argenischacon.dentalclinic.dto.schedule.ScheduleBreakFormDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.model.ScheduleBreak;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final WorkScheduleRepository workScheduleRepository;
    private final DentistRepository dentistRepository;
    private final WorkScheduleMapper workScheduleMapper;
    
    public List<WorkScheduleResponseDto> findAllByDentist(Long dentistId) {
        return workScheduleRepository.findByDentistId(dentistId)
                .stream()
                .map(workScheduleMapper::toResponseDto)
                .toList();
    }

    public void populateScheduleForm(AssignScheduleFormDto scheduleForm) {
        if (scheduleForm.getDentistId() == null) {
            return;
        }

        List<WorkScheduleResponseDto> existingSchedules = findAllByDentist(scheduleForm.getDentistId());

        if (!existingSchedules.isEmpty()) {
            scheduleForm.setSlotDurationMinutes(existingSchedules.getFirst().slotDurationMinutes());
        } else {
            scheduleForm.setSlotDurationMinutes(30);
        }

        Map<DayOfWeek, WorkScheduleResponseDto> dbSchedules = existingSchedules.stream()
                .collect(Collectors.toMap(WorkScheduleResponseDto::dayOfWeek, s -> s));

        scheduleForm.getSchedules().forEach((day, dailyDto) -> {
            WorkScheduleResponseDto dbRecord = dbSchedules.get(day);
            if (dbRecord != null) {
                dailyDto.setStartTime(dbRecord.startTime());
                dailyDto.setEndTime(dbRecord.endTime());
                dailyDto.setAvailable(dbRecord.available());
                if (dbRecord.breaks() != null) {
                    List<ScheduleBreakFormDto> breakForms = dbRecord.breaks().stream()
                            .map(b -> ScheduleBreakFormDto.builder()
                                    .startBreak(b.startBreak())
                                    .endBreak(b.endBreak())
                                    .label(b.label())
                                    .build())
                            .collect(Collectors.toList());
                    dailyDto.setBreaks(breakForms);
                } else {
                    dailyDto.getBreaks().clear();
                }
            } else {
                dailyDto.setStartTime(null);
                dailyDto.setEndTime(null);
                dailyDto.setAvailable(false);
                dailyDto.getBreaks().clear();
            }
        });
    }

    @Transactional
    public void saveDentistSchedule(AssignScheduleFormDto dto) {
        Dentist dentist = dentistRepository.findById(dto.getDentistId())
                .orElseThrow(() -> new BusinessRuleException("Odontólogo no encontrado"));

        dto.getSchedules().forEach((dayOfWeek, dailyDto) -> {
            Optional<WorkSchedule> existingOpt = workScheduleRepository.findByDentistIdAndDayOfWeek(dentist.getId(), dayOfWeek);

            if (dailyDto.isAvailable()) {
                processAvailableDay(dentist, dayOfWeek, dailyDto, dto.getSlotDurationMinutes(), existingOpt);
            } else {
                processUnavailableDay(dentist, dayOfWeek, dailyDto, dto.getSlotDurationMinutes(), existingOpt);
            }
        });
    }

    private void processAvailableDay(Dentist dentist, DayOfWeek dayOfWeek, DailyScheduleFormDto dailyDto, int slotDurationMinutes, Optional<WorkSchedule> existingOpt) {
        validateScheduleTimes(dailyDto);

        WorkSchedule schedule = existingOpt.orElseGet(() -> WorkSchedule.builder()
                .dentist(dentist)
                .dayOfWeek(dayOfWeek)
                .build());

        schedule.setStartTime(dailyDto.getStartTime());
        schedule.setEndTime(dailyDto.getEndTime());
        schedule.setSlotDurationMinutes(slotDurationMinutes);
        schedule.setAvailable(true);

        schedule.getBreaks().clear();
        if (dailyDto.getBreaks() != null) {
            dailyDto.getBreaks().forEach(bDto -> {
                schedule.addBreak(workScheduleMapper.toBreakEntity(bDto));
            });
        }

        workScheduleRepository.save(schedule);
    }

    private void processUnavailableDay(Dentist dentist, DayOfWeek dayOfWeek, DailyScheduleFormDto dailyDto, int slotDurationMinutes, Optional<WorkSchedule> existingOpt) {
        if (existingOpt.isPresent()) {
            WorkSchedule schedule = existingOpt.get();
            schedule.setAvailable(false);
            if (dailyDto.getStartTime() != null && dailyDto.getEndTime() != null) {
                schedule.setStartTime(dailyDto.getStartTime());
                schedule.setEndTime(dailyDto.getEndTime());
            }
            schedule.setSlotDurationMinutes(slotDurationMinutes);
            schedule.getBreaks().clear();
            workScheduleRepository.save(schedule);
        } else if (dailyDto.getStartTime() != null && dailyDto.getEndTime() != null) {
            WorkSchedule schedule = WorkSchedule.builder()
                    .dentist(dentist)
                    .dayOfWeek(dayOfWeek)
                    .startTime(dailyDto.getStartTime())
                    .endTime(dailyDto.getEndTime())
                    .slotDurationMinutes(slotDurationMinutes)
                    .available(false)
                    .build();
            workScheduleRepository.save(schedule);
        }
    }

    private void validateScheduleTimes(DailyScheduleFormDto dailyDto) {
        if (dailyDto.getStartTime() == null || dailyDto.getEndTime() == null) {
            throw new BusinessRuleException("Para días disponibles, la hora de inicio y fin son obligatorias.");
        }
        if (dailyDto.getStartTime().isAfter(dailyDto.getEndTime()) || dailyDto.getStartTime().equals(dailyDto.getEndTime())) {
            throw new BusinessRuleException("La hora de inicio debe ser anterior a la hora de fin.");
        }
        if (dailyDto.getBreaks() != null && dailyDto.getBreaks().size() > 3) {
            throw new BusinessRuleException("Un horario puede tener máximo 3 descansos.");
        }
        if (dailyDto.getBreaks() != null) {
            for (ScheduleBreakFormDto breakDto : dailyDto.getBreaks()) {
                if (breakDto.getStartBreak().isAfter(breakDto.getEndBreak()) || breakDto.getStartBreak().equals(breakDto.getEndBreak())) {
                    throw new BusinessRuleException("La hora de inicio del descanso (" + breakDto.getLabel() + ") debe ser anterior a la hora de fin.");
                }
            }
        }
    }
}
