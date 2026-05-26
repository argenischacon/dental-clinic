package com.argenischacon.dentalclinic.slice.web;

import com.argenischacon.dentalclinic.controller.AdminScheduleController;
import com.argenischacon.dentalclinic.dto.dentist.DentistNestedDto;
import com.argenischacon.dentalclinic.dto.schedule.AssignScheduleFormDto;
import com.argenischacon.dentalclinic.dto.schedule.DailyScheduleFormDto;
import com.argenischacon.dentalclinic.dto.schedule.DailyScheduleViewDto;
import com.argenischacon.dentalclinic.dto.schedule.PreviewItemDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.security.CustomAuthenticationSuccessHandler;
import com.argenischacon.dentalclinic.security.SecurityConfig;
import com.argenischacon.dentalclinic.service.DentistService;
import com.argenischacon.dentalclinic.service.WorkScheduleService;
import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminScheduleController.class)
@Import(SecurityConfig.class)
public class AdminScheduleControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DentistService dentistService;

    @MockBean
    private WorkScheduleService workScheduleService;

    @MockBean
    private CustomAuthenticationSuccessHandler successHandler;

    @BeforeEach
    void setUp() {
        DentistNestedDto dentist1 = new DentistNestedDto(1L, "John Doe", DentalSpecialty.GENERAL);
        when(dentistService.findAllActiveDentists()).thenReturn(List.of(dentist1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignSchedulePage() throws Exception {
        mockMvc.perform(get("/admin/schedules/assign"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/assign"))
                .andExpect(model().attributeExists("scheduleForm"))
                .andExpect(model().attributeExists("dentists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignScheduleForDentistPage_DentistExists() throws Exception {
        when(dentistService.existsById(1L)).thenReturn(true);
        doNothing().when(workScheduleService).populateScheduleForm(any(AssignScheduleFormDto.class));

        mockMvc.perform(get("/admin/schedules/assign/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/assign"))
                .andExpect(model().attributeExists("scheduleForm"));
        
        verify(workScheduleService, times(1)).populateScheduleForm(any(AssignScheduleFormDto.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testAssignScheduleForDentistPage_DentistNotFound() throws Exception {
        when(dentistService.existsById(99L)).thenReturn(false);

        mockMvc.perform(get("/admin/schedules/assign/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/schedules/assign"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testViewSchedulePage() throws Exception {
        mockMvc.perform(get("/admin/schedules/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/view"))
                .andExpect(model().attributeExists("dentists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testViewScheduleForDentistPage_DentistExists() throws Exception {
        when(dentistService.existsById(1L)).thenReturn(true);
        Map<DayOfWeek, DailyScheduleViewDto> weeklySchedule = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            weeklySchedule.put(day, new DailyScheduleViewDto(false, null, null, Collections.emptyList()));
        }
        when(workScheduleService.getWeeklyScheduleView(1L)).thenReturn(weeklySchedule);

        mockMvc.perform(get("/admin/schedules/view/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/view"))
                .andExpect(model().attribute("weeklySchedule", weeklySchedule))
                .andExpect(model().attribute("dentistId", 1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testViewScheduleForDentistPage_DentistNotFound() throws Exception {
        when(dentistService.existsById(99L)).thenReturn(false);

        mockMvc.perform(get("/admin/schedules/view/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/schedules/view"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetScheduleTable() throws Exception {
        when(dentistService.existsById(1L)).thenReturn(true);
        Map<DayOfWeek, DailyScheduleViewDto> weeklySchedule = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            weeklySchedule.put(day, new DailyScheduleViewDto(false, null, null, Collections.emptyList()));
        }
        when(workScheduleService.getWeeklyScheduleView(1L)).thenReturn(weeklySchedule);

        mockMvc.perform(get("/admin/schedules/view/table").param("dentistId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/_view_table :: scheduleTable"))
                .andExpect(model().attributeExists("weeklySchedule"))
                .andExpect(model().attributeExists("dentistId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveSchedule_Success() throws Exception {
        doNothing().when(workScheduleService).saveDentistSchedule(any(AssignScheduleFormDto.class));

        mockMvc.perform(post("/admin/schedules/assign")
                        .with(csrf())
                        .param("dentistId", "1")
                        .param("slotDurationMinutes", "30")
                        .param("schedules[MONDAY].available", "true")
                        .param("schedules[MONDAY].startTime", "08:00")
                        .param("schedules[MONDAY].endTime", "16:00")
                        .param("schedules[MONDAY].dayOfWeek", "MONDAY")
                        .param("schedules[TUESDAY].available", "false")
                        .param("schedules[WEDNESDAY].available", "false")
                        .param("schedules[THURSDAY].available", "false")
                        .param("schedules[FRIDAY].available", "false")
                        .param("schedules[SATURDAY].available", "false")
                        .param("schedules[SUNDAY].available", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/schedules/assign"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveSchedule_ValidationError() throws Exception {
        mockMvc.perform(post("/admin/schedules/assign")
                        .with(csrf())
                        // missing dentistId triggers @NotNull validation
                        .param("slotDurationMinutes", "30"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/assign"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveSchedule_BusinessRuleException() throws Exception {
        doThrow(new BusinessRuleException("Error guardando horario"))
                .when(workScheduleService).saveDentistSchedule(any(AssignScheduleFormDto.class));

        mockMvc.perform(post("/admin/schedules/assign")
                        .with(csrf())
                        .param("dentistId", "1")
                        .param("slotDurationMinutes", "30")
                        .param("schedules[MONDAY].available", "true")
                        .param("schedules[MONDAY].startTime", "08:00")
                        .param("schedules[MONDAY].endTime", "16:00")
                        .param("schedules[MONDAY].dayOfWeek", "MONDAY")
                        .param("schedules[TUESDAY].available", "false")
                        .param("schedules[WEDNESDAY].available", "false")
                        .param("schedules[THURSDAY].available", "false")
                        .param("schedules[FRIDAY].available", "false")
                        .param("schedules[SATURDAY].available", "false")
                        .param("schedules[SUNDAY].available", "false"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/assign"))
                .andExpect(model().attribute("error", "Error guardando horario"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPreviewSchedule_Success() throws Exception {
        List<PreviewItemDto> items = List.of(
                new PreviewItemDto(LocalTime.of(8, 0), LocalTime.of(8, 30), false, "Disponible")
        );
        when(workScheduleService.generateChronologicalPreview(any(DailyScheduleFormDto.class), anyInt()))
                .thenReturn(items);

        mockMvc.perform(post("/admin/schedules/preview")
                        .with(csrf())
                        .param("day", "MONDAY")
                        .param("dentistId", "1")
                        .param("slotDurationMinutes", "30")
                        .param("schedules[MONDAY].available", "true")
                        .param("schedules[MONDAY].startTime", "08:00")
                        .param("schedules[MONDAY].endTime", "16:00")
                        .param("schedules[MONDAY].dayOfWeek", "MONDAY"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/_preview_fragment :: preview"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("day"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testPreviewSchedule_DayNotAvailable() throws Exception {
        mockMvc.perform(post("/admin/schedules/preview")
                        .with(csrf())
                        .param("day", "MONDAY")
                        .param("dentistId", "1")
                        .param("slotDurationMinutes", "30")
                        .param("schedules[MONDAY].available", "false")
                        .param("schedules[MONDAY].dayOfWeek", "MONDAY"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/_preview_fragment :: preview"))
                .andExpect(model().attributeExists("previewError"));
    }
}
