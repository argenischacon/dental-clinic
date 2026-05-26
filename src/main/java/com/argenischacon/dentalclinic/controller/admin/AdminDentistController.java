package com.argenischacon.dentalclinic.controller.admin;

import com.argenischacon.dentalclinic.dto.dentist.DentistRequestDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.service.DentistService;
import jakarta.servlet.ServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/dentists")
@RequiredArgsConstructor
public class AdminDentistController {

    private final DentistService dentistService;

    @GetMapping("/list")
    public String dentistListPage(
            Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {

        model.addAttribute("dentistsPage",
                dentistService.findAllDentists(search, specialty, active, pageable));

        var stats = dentistService.getStats();
        model.addAttribute("totalDentists", stats.totalDentists());
        model.addAttribute("activeDentists", stats.activeDentists());
        model.addAttribute("inactiveDentists", stats.inactiveDentists());

        return "admin/dentists/list";
    }

    @GetMapping("/add")
    public String dentistAdd(@ModelAttribute DentistRequestDto dentistRequestDto) {
        return "admin/dentists/add";
    }

    @PostMapping("/save")
    public String saveDentist(@Valid @ModelAttribute DentistRequestDto dentistRequestDto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/dentists/add";
        }

        try {
            dentistService.dentistAdd(dentistRequestDto);
            redirectAttributes.addFlashAttribute("success", "El odontólogo ha sido registrado exitosamente.");
        } catch (BusinessRuleException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/dentists/add";
        }

        return "redirect:/admin/dentists/list";
    }

    @GetMapping("/{id}/detail")
    public String getDentistDetailModal(@PathVariable Long id, Model model, ServletResponse servletResponse) {
        model.addAttribute("dentist", dentistService.findById(id));
        return "admin/dentists/_detail :: modal-content";
    }

    @GetMapping("/edit/{id}")
    public String dentistEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("dentistRequestDto", dentistService.getDentistForEdit(id));
        model.addAttribute("dentistId", id);
        return "admin/dentists/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateDentist(@PathVariable Long id,
                                @Valid @ModelAttribute DentistRequestDto dentistRequestDto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("dentistId", id);
            return "admin/dentists/edit";
        }
        try {
            dentistService.updateDentist(id, dentistRequestDto);
            redirectAttributes.addFlashAttribute("success", "Odontólogo actualizado exitosamente.");
        } catch (BusinessRuleException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("dentistId", id);
            return "admin/dentists/edit";
        }
        return "redirect:/admin/dentists/list";
    }

    @PostMapping("/activate/{id}")
    public String activateDentist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        dentistService.activateDentist(id);
        redirectAttributes.addFlashAttribute("success", "Odontólogo activado exitosamente.");
        return "redirect:/admin/dentists/list";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateDentist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        dentistService.deactivateDentist(id);
        redirectAttributes.addFlashAttribute("success", "Odontólogo desactivado exitosamente.");
        return "redirect:/admin/dentists/list";
    }
}
