package com.argenischacon.dentalclinic.controller;

import com.argenischacon.dentalclinic.dto.schedule.AssignScheduleFormDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistNestedDto;
import com.argenischacon.dentalclinic.service.DentistService;
import com.argenischacon.dentalclinic.service.WorkScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
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
                               org.springframework.ui.Model model,
                               RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()){
            model.addAttribute("error", "Por favor, revise los datos del formulario.");
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
}
