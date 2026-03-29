package com.argenischacon.dentalclinic.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "app.admin.default")
@Validated
@Getter
@Setter
public class AdminProperties {

    @NotBlank(message = "El nombre de usuario del administrador por defecto no puede estar vacío.")
    private String username = "admin";

    @NotBlank(message = "La contraseña del administrador por defecto no puede estar vacía. " +
            "Asegúrese de definir ADMIN_DEFAULT_PASSWORD en las variables de entorno (especialmente en producción).")
    private String password;
}
