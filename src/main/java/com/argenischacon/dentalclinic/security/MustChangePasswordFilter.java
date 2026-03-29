package com.argenischacon.dentalclinic.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MustChangePasswordFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        
        // Skip all static resources and auth endpoints from being trapped.
        // Must mirror the permitAll() paths defined in SecurityConfig.
        if (uri.startsWith("/css/") || uri.startsWith("/js/") || uri.startsWith("/img/")
            || uri.startsWith("/images/") || uri.startsWith("/vendor/") || uri.startsWith("/scss/")
            || uri.startsWith("/webjars/") || uri.startsWith("/h2-console")
            || uri.startsWith("/login") || uri.startsWith("/logout")
            || uri.startsWith("/change-password") || uri.startsWith("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            if (userDetails.isMustChangePassword()) {
                response.sendRedirect(request.getContextPath() + "/change-password");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
