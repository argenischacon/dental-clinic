package com.argenischacon.dentalclinic.controller.admin;

import com.argenischacon.dentalclinic.dto.service.ServiceRequestDto;
import com.argenischacon.dentalclinic.dto.service.ServiceListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    @GetMapping("/list")
    public String serviceListPage(
            Model model,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean active,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        Page<ServiceListDto> servicesPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        model.addAttribute("servicesPage", servicesPage);
        model.addAttribute("totalServices", 0);
        model.addAttribute("activeServices", 0);
        model.addAttribute("inactiveServices", 0);

        return "admin/services/list";
    }

    @GetMapping("/add")
    public String addService(@ModelAttribute ServiceRequestDto serviceRequestDto) {
        return "admin/services/add";
    }
}
