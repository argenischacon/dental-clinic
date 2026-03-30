package com.argenischacon.dentalclinic.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SavedRequestAwareAuthenticationSuccessHandler defaultHandler = new SavedRequestAwareAuthenticationSuccessHandler();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (userDetails.isMustChangePassword()) {
            response.sendRedirect(request.getContextPath() + "/change-password");
        } else {
            String targetUrl = "/";
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                String role = authority.getAuthority();
                if ("ROLE_ADMIN".equals(role)) {
                    targetUrl = "/admin/dashboard";
                    break;
                } else if ("ROLE_DENTIST".equals(role)) {
                    targetUrl = "/dentist/dashboard";
                    break;
                } else if ("ROLE_RECEPTIONIST".equals(role)) {
                    targetUrl = "/receptionist/dashboard";
                    break;
                }
            }
            defaultHandler.setDefaultTargetUrl(targetUrl);
            defaultHandler.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
