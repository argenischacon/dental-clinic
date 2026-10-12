package com.argenischacon.dentalclinic.controller.admin;

import com.argenischacon.dentalclinic.dto.treatment.TreatmentRequestDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.service.TreatmentService;
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
@RequestMapping("/admin/treatments")
@RequiredArgsConstructor
public class AdminTreatmentController {

    private final TreatmentService treatmentService;

    @GetMapping("/list")
    public String treatmentListPage(
            Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        model.addAttribute("treatmentsPage",
                treatmentService.findAllTreatments(search, active, pageable));

        var stats = treatmentService.getStats();
        model.addAttribute("totalTreatments", stats.totalTreatments());
        model.addAttribute("activeTreatments", stats.activeTreatments());
        model.addAttribute("inactiveTreatments", stats.inactiveTreatments());

        return "admin/treatments/list";
    }

    @GetMapping("/add")
    public String addTreatment(@ModelAttribute TreatmentRequestDto treatmentRequestDto) {
        return "admin/treatments/add";
    }

    @PostMapping("/save")
    public String saveTreatment(@Valid @ModelAttribute TreatmentRequestDto treatmentRequestDto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/treatments/add";
        }

        try {
            treatmentService.treatmentAdd(treatmentRequestDto);
            redirectAttributes.addFlashAttribute("success", "El tratamiento ha sido registrado exitosamente.");
        } catch (BusinessRuleException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/treatments/add";
        }

        return "redirect:/admin/treatments/list";
    }

    @GetMapping("/{id}/detail")
    public String getTreatmentDetailModal(@PathVariable Long id, Model model) {
        model.addAttribute("treatment", treatmentService.findById(id));
        return "admin/treatments/_detail :: modal-content";
    }

    @GetMapping("/edit/{id}")
    public String treatmentEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("treatmentRequestDto", treatmentService.getTreatmentForEdit(id));
        model.addAttribute("treatmentId", id);
        return "admin/treatments/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateTreatment(@PathVariable Long id,
                                @Valid @ModelAttribute TreatmentRequestDto treatmentRequestDto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("treatmentId", id);
            return "admin/treatments/edit";
        }
        try {
            treatmentService.updateTreatment(id, treatmentRequestDto);
            redirectAttributes.addFlashAttribute("success", "Tratamiento actualizado exitosamente.");
        } catch (BusinessRuleException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("treatmentId", id);
            return "admin/treatments/edit";
        }
        return "redirect:/admin/treatments/list";
    }

    @PostMapping("/activate/{id}")
    public String activateTreatment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        treatmentService.activateTreatment(id);
        redirectAttributes.addFlashAttribute("success", "Tratamiento activado exitosamente.");
        return "redirect:/admin/treatments/list";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateTreatment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        treatmentService.deactivateTreatment(id);
        redirectAttributes.addFlashAttribute("success", "Tratamiento desactivado exitosamente.");
        return "redirect:/admin/treatments/list";
    }
}
