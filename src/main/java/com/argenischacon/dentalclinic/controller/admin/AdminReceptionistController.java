package com.argenischacon.dentalclinic.controller.admin;

import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistRequestDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.service.ReceptionistService;
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
@RequestMapping("/admin/receptionists")
@RequiredArgsConstructor
public class AdminReceptionistController {

    private final ReceptionistService receptionistService;

    @GetMapping("/list")
    public String receptionistListPage(
            Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "lastName") Pageable pageable) {

        model.addAttribute("receptionistsPage",
                receptionistService.findAllReceptionists(search, active, pageable));

        var stats = receptionistService.getStats();
        model.addAttribute("totalReceptionists", stats.totalReceptionists());
        model.addAttribute("activeReceptionists", stats.activeReceptionists());
        model.addAttribute("inactiveReceptionists", stats.inactiveReceptionists());

        return "admin/receptionists/list";
    }

    @GetMapping("/add")
    public String receptionistAdd(Model model) {
        model.addAttribute("receptionistRequestDto", ReceptionistRequestDto.builder().build());
        return "admin/receptionists/add";
    }

    @PostMapping("/save")
    public String saveReceptionist(@Valid @ModelAttribute ReceptionistRequestDto receptionistRequestDto,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/receptionists/add";
        }

        try {
            receptionistService.receptionistAdd(receptionistRequestDto);
            redirectAttributes.addFlashAttribute("success", "El/La recepcionista ha sido registrado/a exitosamente.");
        } catch (BusinessRuleException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/receptionists/add";
        }

        return "redirect:/admin/receptionists/list";
    }

    @GetMapping("/{id}/detail")
    public String getReceptionistDetailModal(@PathVariable Long id, Model model, ServletResponse servletResponse) {
        model.addAttribute("receptionist", receptionistService.findById(id));
        return "admin/receptionists/_detail :: modal-content";
    }

    @GetMapping("/edit/{id}")
    public String receptionistEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("receptionistRequestDto", receptionistService.getReceptionistForEdit(id));
        model.addAttribute("receptionistId", id);
        return "admin/receptionists/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateReceptionist(@PathVariable Long id,
                                     @Valid @ModelAttribute ReceptionistRequestDto receptionistRequestDto,
                                     BindingResult result,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("receptionistId", id);
            return "admin/receptionists/edit";
        }
        try {
            receptionistService.updateReceptionist(id, receptionistRequestDto);
            redirectAttributes.addFlashAttribute("success", "Recepcionista actualizado/a exitosamente.");
        } catch (BusinessRuleException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("receptionistId", id);
            return "admin/receptionists/edit";
        }
        return "redirect:/admin/receptionists/list";
    }

    @PostMapping("/activate/{id}")
    public String activateReceptionist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        receptionistService.activateReceptionist(id);
        redirectAttributes.addFlashAttribute("success", "Recepcionista activado/a exitosamente.");
        return "redirect:/admin/receptionists/list";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateReceptionist(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        receptionistService.deactivateReceptionist(id);
        redirectAttributes.addFlashAttribute("success", "Recepcionista desactivado/a exitosamente.");
        return "redirect:/admin/receptionists/list";
    }
}
