package com.argenischacon.dentalclinic.bootstrap;

import com.argenischacon.dentalclinic.enums.Role;
import com.argenischacon.dentalclinic.model.User;
import com.argenischacon.dentalclinic.repository.UserRepository;
import com.argenischacon.dentalclinic.config.AdminProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRole(Role.ADMIN)) {
            String encodedPassword = passwordEncoder.encode(adminProperties.getPassword());
            
            User admin = User.builder()
                    .username(adminProperties.getUsername())
                    .password(encodedPassword)
                    .role(Role.ADMIN)
                    .mustChangePassword(true)
                    .active(true)
                    .build();
            
            userRepository.save(admin);
            
            log.info("Cuenta de administrador por defecto ('{}') creada exitosamente.", adminProperties.getUsername());
            log.info("Se ha establecido su contraseña inicial desde la configuración. Debe cambiarla en su primer inicio de sesión.");
        }
    }
}
