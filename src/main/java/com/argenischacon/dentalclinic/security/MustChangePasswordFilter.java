package com.argenischacon.dentalclinic.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class MustChangePasswordFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        // Exclude static resources in common directories
        boolean isStaticDirectory = path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/img/")
                || path.startsWith("/images/") || path.startsWith("/vendor/") || path.startsWith("/scss/")
                || path.startsWith("/webjars/");

        // Exclude favicon and manifest files at the root
        boolean isFaviconResource = path.equals("/favicon.ico") || path.equals("/favicon.svg")
                || path.equals("/apple-touch-icon.png") || path.equals("/site.webmanifest")
                || path.startsWith("/favicon-") || path.startsWith("/web-app-manifest-");

        // Exclude authentication and console paths
        boolean isAuthPath = path.startsWith("/login") || path.startsWith("/logout")
                || path.startsWith("/change-password") || path.startsWith("/error")
                || path.startsWith("/h2-console");

        return isStaticDirectory || isFaviconResource || isAuthPath;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

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
