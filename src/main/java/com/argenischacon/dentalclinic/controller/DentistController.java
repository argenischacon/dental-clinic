package com.argenischacon.dentalclinic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DentistController {

    @GetMapping("/dentist/dashboard")
    public String dashboard() {
        return "dentist/dashboard";
    }
}
