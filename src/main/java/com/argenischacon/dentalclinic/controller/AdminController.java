package com.argenischacon.dentalclinic.controller;

import com.argenischacon.dentalclinic.dto.dentist.DentistRequestDto;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DentistService dentistService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/dentists/list")
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

    @GetMapping("/dentists/add")
    public String dentistAdd(@ModelAttribute DentistRequestDto dentistRequestDto) {
        return "admin/dentists/add";
    }

    @PostMapping("/dentists/save")
    public String saveDentist(@Valid @ModelAttribute DentistRequestDto dentistRequestDto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/dentists/add";
        }

        dentistService.dentistAdd(dentistRequestDto);

        redirectAttributes.addFlashAttribute("success", "El odontólogo ha sido registrado exitosamente.");

        return "redirect:/admin/dentists/list";
    }

    @GetMapping("/dentists/{id}/detail")
    public String getDentistDetailModal(@PathVariable Long id, Model model, ServletResponse servletResponse) {
        model.addAttribute("dentist", dentistService.findById(id));
        return "admin/dentists/_detail :: modal-content";
    }
}