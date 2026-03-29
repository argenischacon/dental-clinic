package com.argenischacon.dentalclinic.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Captura Errores 404 (Rutas y Recursos No Encontrados en Spring Boot 3.2+)
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNotFoundException(NoResourceFoundException ex, Model model) {
        logger.warn("404 Not Found: {}", ex.getResourcePath());
        model.addAttribute("errorMessage", "La página que buscas no existe.");
        return "error/404";
    }

    // Captura Accesos Denegados de Spring Security (403)
    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model) {
        logger.warn("403 Access Denied: {}", ex.getMessage());
        model.addAttribute("errorMessage", "No tienes permisos suficientes para acceder a este recurso.");
        return "error/403";
    }

    // Captura cualquier otra Excepción (500)
    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, Model model) {
        logger.error("500 Internal Server Error:", ex);
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado en el servidor.");
        return "error/500";
    }
}
