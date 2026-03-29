package com.argenischacon.dentalclinic.controller;

import com.argenischacon.dentalclinic.dto.user.ChangePasswordRequest;
import com.argenischacon.dentalclinic.security.CustomUserDetails;
import com.argenischacon.dentalclinic.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/change-password")
    public String changePasswordPage(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }
        model.addAttribute("changePasswordRequest", new ChangePasswordRequest(null, null));
        return "auth/change-password";
    }

    @PostMapping("/change-password")
    public String processChangePassword(
            @Valid @ModelAttribute("changePasswordRequest") ChangePasswordRequest changePasswordRequest,
            BindingResult bindingResult,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            return "auth/change-password";
        }

        try {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            userService.changePassword(userDetails.getUser().getId(), changePasswordRequest);
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            return "redirect:/login?passwordChanged";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/change-password";
        }
    }
}
