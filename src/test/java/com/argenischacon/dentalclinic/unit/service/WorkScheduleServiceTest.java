package com.argenischacon.dentalclinic.unit.service;

import com.argenischacon.dentalclinic.dto.schedule.*;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.mappers.WorkScheduleMapper;
import com.argenischacon.dentalclinic.model.Dentist;
import com.argenischacon.dentalclinic.model.TimeSlot;
import com.argenischacon.dentalclinic.model.WorkSchedule;
import com.argenischacon.dentalclinic.repository.DentistRepository;
import com.argenischacon.dentalclinic.repository.WorkScheduleRepository;
import com.argenischacon.dentalclinic.service.WorkScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkScheduleServiceTest {

    @Mock
    private WorkScheduleRepository workScheduleRepository;

    @Mock
    private DentistRepository dentistRepository;

    @Mock
    private WorkScheduleMapper workScheduleMapper;

    @InjectMocks
    private WorkScheduleService workScheduleService;

    private Dentist defaultDentist;

    @BeforeEach
    void setUp() {
        defaultDentist = new Dentist();
        defaultDentist.setId(1L);
        defaultDentist.setName("John");
        defaultDentist.setLastName("Doe");
    }

    @Test
    void testFindAllByDentist() {
        WorkSchedule schedule = new WorkSchedule();
        schedule.setId(1L);
        when(workScheduleRepository.findByDentistId(1L)).thenReturn(List.of(schedule));
        
        WorkScheduleResponseDto responseDto = new WorkScheduleResponseDto(
                1L, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0), 30, true, Collections.emptyList());
        when(workScheduleMapper.toResponseDto(schedule)).thenReturn(responseDto);

        List<WorkScheduleResponseDto> result = workScheduleService.findAllByDentist(1L);
        
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).id());
        verify(workScheduleRepository).findByDentistId(1L);
    }

    @Test
    void testPopulateScheduleForm_WithExistingSchedules() {
        AssignScheduleFormDto form = new AssignScheduleFormDto();
        form.setDentistId(1L);
        
        WorkScheduleResponseDto existingSchedule = new WorkScheduleResponseDto(
                1L, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0), 30, true, Collections.emptyList());
        
        WorkSchedule scheduleEntity = new WorkSchedule();
        scheduleEntity.setId(1L);
        when(workScheduleRepository.findByDentistId(1L)).thenReturn(List.of(scheduleEntity));
        when(workScheduleMapper.toResponseDto(any())).thenReturn(existingSchedule);

        workScheduleService.populateScheduleForm(form);

        assertEquals(30, form.getSlotDurationMinutes());
        assertTrue(form.getSchedules().get(DayOfWeek.MONDAY).isAvailable());
        assertEquals(LocalTime.of(8, 0), form.getSchedules().get(DayOfWeek.MONDAY).getStartTime());
        assertFalse(form.getSchedules().get(DayOfWeek.TUESDAY).isAvailable());
    }

    @Test
    void testSaveDentistSchedule_NoAvailableDays_ThrowsException() {
        AssignScheduleFormDto form = new AssignScheduleFormDto();
        form.setDentistId(1L);
        form.getSchedules().values().forEach(daily -> daily.setAvailable(false));

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            workScheduleService.saveDentistSchedule(form);
        });

        assertEquals("Debe configurar al menos un día disponible para guardar el horario de trabajo.", exception.getMessage());
    }

    @Test
    void testSaveDentistSchedule_DentistNotFound_ThrowsException() {
        AssignScheduleFormDto form = new AssignScheduleFormDto();
        form.setDentistId(1L);
        form.getSchedules().get(DayOfWeek.MONDAY).setAvailable(true);
        
        when(dentistRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            workScheduleService.saveDentistSchedule(form);
        });

        assertEquals("Odontólogo no encontrado", exception.getMessage());
    }

    @Test
    void testSaveDentistSchedule_Success() {
        AssignScheduleFormDto form = new AssignScheduleFormDto();
        form.setDentistId(1L);
        form.setSlotDurationMinutes(30);
        
        for (DayOfWeek day : DayOfWeek.values()) {
            form.getSchedules().get(day).setAvailable(day == DayOfWeek.MONDAY);
        }
        
        DailyScheduleFormDto monday = form.getSchedules().get(DayOfWeek.MONDAY);
        monday.setStartTime(LocalTime.of(8, 0));
        monday.setEndTime(LocalTime.of(16, 0));
        
        when(dentistRepository.findById(1L)).thenReturn(Optional.of(defaultDentist));
        when(workScheduleRepository.findByDentistIdAndDayOfWeek(eq(1L), any(DayOfWeek.class))).thenReturn(Optional.empty());

        workScheduleService.saveDentistSchedule(form);

        verify(workScheduleRepository, times(1)).save(any(WorkSchedule.class));
    }

    @Test
    void testCalculatePreviewSlots() {
        DailyScheduleFormDto dailyDto = new DailyScheduleFormDto();
        dailyDto.setStartTime(LocalTime.of(8, 0));
        dailyDto.setEndTime(LocalTime.of(10, 0));
        
        ScheduleBreakFormDto breakDto = new ScheduleBreakFormDto();
        breakDto.setStartBreak(LocalTime.of(9, 0));
        breakDto.setEndBreak(LocalTime.of(9, 30));
        breakDto.setLabel("Descanso");
        dailyDto.setBreaks(List.of(breakDto));

        List<TimeSlot> slots = workScheduleService.calculatePreviewSlots(dailyDto, 30);
        
        assertEquals(3, slots.size()); // 8:00-8:30, 8:30-9:00, 9:30-10:00
        assertEquals(LocalTime.of(8, 0), slots.get(0).getStart());
        assertEquals(LocalTime.of(9, 30), slots.get(2).getStart());
    }

    @Test
    void testGenerateChronologicalPreview() {
        DailyScheduleFormDto dailyDto = new DailyScheduleFormDto();
        dailyDto.setStartTime(LocalTime.of(8, 0));
        dailyDto.setEndTime(LocalTime.of(9, 0));
        
        ScheduleBreakFormDto breakDto = new ScheduleBreakFormDto();
        breakDto.setStartBreak(LocalTime.of(8, 30));
        breakDto.setEndBreak(LocalTime.of(9, 0));
        breakDto.setLabel("Reunión");
        dailyDto.setBreaks(List.of(breakDto));

        List<PreviewItemDto> items = workScheduleService.generateChronologicalPreview(dailyDto, 30);

        assertEquals(2, items.size()); // 1 slot + 1 break
        assertEquals(LocalTime.of(8, 0), items.get(0).start());
        assertFalse(items.get(0).isBreak());
        assertEquals(LocalTime.of(8, 30), items.get(1).start());
        assertTrue(items.get(1).isBreak());
        assertEquals("Reunión", items.get(1).label());
    }
}
