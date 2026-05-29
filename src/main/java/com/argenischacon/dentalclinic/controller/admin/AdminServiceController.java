package com.argenischacon.dentalclinic.controller.admin;

import com.argenischacon.dentalclinic.dto.service.ServiceRequestDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.service.ServiceService;
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
@RequestMapping("/admin/services")
@RequiredArgsConstructor
public class AdminServiceController {

    private final ServiceService serviceService;

    @GetMapping("/list")
    public String serviceListPage(
            Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        model.addAttribute("servicesPage",
                serviceService.findAllServices(search, active, pageable));

        var stats = serviceService.getStats();
        model.addAttribute("totalServices", stats.totalServices());
        model.addAttribute("activeServices", stats.activeServices());
        model.addAttribute("inactiveServices", stats.inactiveServices());

        return "admin/services/list";
    }

    @GetMapping("/add")
    public String addService(@ModelAttribute ServiceRequestDto serviceRequestDto) {
        return "admin/services/add";
    }

    @PostMapping("/save")
    public String saveService(@Valid @ModelAttribute ServiceRequestDto serviceRequestDto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/services/add";
        }

        try {
            serviceService.serviceAdd(serviceRequestDto);
            redirectAttributes.addFlashAttribute("success", "El servicio ha sido registrado exitosamente.");
        } catch (BusinessRuleException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/services/add";
        }

        return "redirect:/admin/services/list";
    }

    @GetMapping("/{id}/detail")
    public String getServiceDetailModal(@PathVariable Long id, Model model) {
        model.addAttribute("service", serviceService.findById(id));
        return "admin/services/_detail :: modal-content";
    }

    @GetMapping("/edit/{id}")
    public String serviceEditPage(@PathVariable Long id, Model model) {
        model.addAttribute("serviceRequestDto", serviceService.getServiceForEdit(id));
        model.addAttribute("serviceId", id);
        return "admin/services/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateService(@PathVariable Long id,
                                @Valid @ModelAttribute ServiceRequestDto serviceRequestDto,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("serviceId", id);
            return "admin/services/edit";
        }
        try {
            serviceService.updateService(id, serviceRequestDto);
            redirectAttributes.addFlashAttribute("success", "Servicio actualizado exitosamente.");
        } catch (BusinessRuleException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("serviceId", id);
            return "admin/services/edit";
        }
        return "redirect:/admin/services/list";
    }

    @PostMapping("/activate/{id}")
    public String activateService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        serviceService.activateService(id);
        redirectAttributes.addFlashAttribute("success", "Servicio activado exitosamente.");
        return "redirect:/admin/services/list";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        serviceService.deactivateService(id);
        redirectAttributes.addFlashAttribute("success", "Servicio desactivado exitosamente.");
        return "redirect:/admin/services/list";
    }
}
