package com.argenischacon.dentalclinic.controller.dentist;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DentistDashboardController {

    @GetMapping("/dentist/dashboard")
    public String dashboard() {
        return "dentist/dashboard";
    }
}
