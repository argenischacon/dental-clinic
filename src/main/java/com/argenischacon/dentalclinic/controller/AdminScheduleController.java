package com.argenischacon.dentalclinic.controller;

import com.argenischacon.dentalclinic.dto.schedule.AssignScheduleFormDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistNestedDto;
import com.argenischacon.dentalclinic.dto.schedule.DailyScheduleFormDto;
import com.argenischacon.dentalclinic.dto.schedule.PreviewItemDto;
import com.argenischacon.dentalclinic.model.TimeSlot;
import com.argenischacon.dentalclinic.service.DentistService;
import com.argenischacon.dentalclinic.service.WorkScheduleService;
import jakarta.validation.ConstraintViolation;
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
import java.util.Set;
import java.util.stream.Collectors;

import com.argenischacon.dentalclinic.exception.BusinessRuleException;

@Controller
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final DentistService dentistService;
    private final WorkScheduleService workScheduleService;
    private final jakarta.validation.Validator validator;

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

        Set<ConstraintViolation<DailyScheduleFormDto>> violations = validator.validate(dailyDto);
        
        if (!violations.isEmpty()) {
            String errorMsg = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining("<br>"));
            model.addAttribute("previewError", errorMsg);
            return "admin/schedules/_preview_fragment :: preview";
        }

        List<PreviewItemDto> items = workScheduleService.generateChronologicalPreview(dailyDto, scheduleForm.getSlotDurationMinutes());
        
        model.addAttribute("items", items);
        model.addAttribute("day", day);
        return "admin/schedules/_preview_fragment :: preview";
    }
}
