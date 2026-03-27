package com.argenischacon.dentalclinic.bootstrap;

import com.argenischacon.dentalclinic.enums.Role;
import com.argenischacon.dentalclinic.model.User;
import com.argenischacon.dentalclinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByRole(Role.ADMIN)) {
            // Nota: Al no haber un UserService complejo implementado aún en esta etapa del desarrollo,
            // el inicializador en AdminBootstrap usará el UserRepository directamente para generar 
            // el usuario administrador por defecto de forma temporal.
            
            String temporaryPassword = UUID.randomUUID().toString().substring(0, 8);
            
            User admin = User.builder()
                    .username("admin")
                    .password(temporaryPassword)
                    .role(Role.ADMIN)
                    .mustChangePassword(true)
                    .active(true)
                    .build();
            
            userRepository.save(admin);
            
            log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            log.info("Default ADMIN user created successfully.");
            log.info("Username: admin");
            log.info("Temporary Password: {}", temporaryPassword);
            log.info("Please change this password upon first login.");
            log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }
    }
}
