package com.argenischacon.dentalclinic.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/")
    public String dashboard(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                if ("ROLE_ADMIN".equals(role)) {
                    return "redirect:/admin/dashboard";
                } else if ("ROLE_DENTIST".equals(role)) {
                    return "redirect:/dentist/dashboard";
                } else if ("ROLE_RECEPTIONIST".equals(role)) {
                    return "redirect:/receptionist/dashboard";
                }
            }
        }
        return "redirect:/login";
    }

}
