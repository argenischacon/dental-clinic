package com.argenischacon.dentalclinic.controller;

import com.argenischacon.dentalclinic.dto.schedule.AssignScheduleFormDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistNestedDto;
import com.argenischacon.dentalclinic.dto.schedule.DailyScheduleFormDto;
import com.argenischacon.dentalclinic.dto.schedule.ScheduleBreakFormDto;
import com.argenischacon.dentalclinic.model.TimeSlot;
import com.argenischacon.dentalclinic.service.DentistService;
import com.argenischacon.dentalclinic.service.WorkScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.util.List;
import java.util.stream.Collectors;

import com.argenischacon.dentalclinic.exception.BusinessRuleException;

@Controller
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final DentistService dentistService;
    private final WorkScheduleService workScheduleService;

    @ModelAttribute("dentists")
    public List<DentistNestedDto> getActiveDentists() {
        return dentistService.findAllActiveDentists();
    }

    @GetMapping("/assign")
    public String assignSchedulePage(@ModelAttribute(name = "scheduleForm") AssignScheduleFormDto scheduleForm) {
        return "admin/schedules/assign";
    }

    @PostMapping("/assign")
    public String saveSchedule(@Valid @ModelAttribute(name = "scheduleForm") AssignScheduleFormDto scheduleForm,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()){
            String errorMsg = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("<br>"));
            model.addAttribute("error", errorMsg);
            return "admin/schedules/assign";
        }

        try {
            workScheduleService.saveDentistSchedule(scheduleForm);
            redirectAttributes.addFlashAttribute("success", "Horario guardado correctamente");
            return "redirect:/admin/schedules/assign";
        } catch (BusinessRuleException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/schedules/assign";
        } catch (Exception e) {
            model.addAttribute("error", "Ocurrió un error al guardar el horario.");
            return "admin/schedules/assign";
        }
    }

    @GetMapping("/tabs")
    public String getScheduleTabs(@ModelAttribute("scheduleForm") AssignScheduleFormDto scheduleForm) {
        workScheduleService.populateScheduleForm(scheduleForm);
        return "admin/schedules/_schedule_tabs :: tabs";
    }

    @PostMapping("/preview")
    public String previewSchedule(@ModelAttribute("scheduleForm") AssignScheduleFormDto scheduleForm,
                                  @RequestParam("day") DayOfWeek day,
                                  Model model) {
        
        DailyScheduleFormDto dailyDto = scheduleForm.getSchedules().get(day);
        
        if (dailyDto == null || !dailyDto.isAvailable()) {
            model.addAttribute("previewError", "El día no está marcado como disponible.");
            return "admin/schedules/_preview_fragment :: preview";
        }

        if (!dailyDto.isTimePresentIfAvailable() || !dailyDto.isValidTimeRange()) {
            model.addAttribute("previewError", "La hora de inicio y fin son obligatorias y la hora de inicio debe ser anterior a la de fin.");
            return "admin/schedules/_preview_fragment :: preview";
        }

        boolean validBreaks = true;
        if (dailyDto.getBreaks() != null) {
            validBreaks = dailyDto.getBreaks().stream()
                .allMatch(ScheduleBreakFormDto::isValidTimeRange);
        }
        
        if (!validBreaks) {
            model.addAttribute("previewError", "Los descansos tienen horas inválidas.");
            return "admin/schedules/_preview_fragment :: preview";
        }

        List<TimeSlot> slots = workScheduleService.calculatePreviewSlots(dailyDto, scheduleForm.getSlotDurationMinutes());
        
        model.addAttribute("slots", slots);
        model.addAttribute("breaks", dailyDto.getBreaks());
        model.addAttribute("day", day);
        return "admin/schedules/_preview_fragment :: preview";
    }
}
