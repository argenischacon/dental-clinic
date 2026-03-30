package com.argenischacon.dentalclinic.security;

import com.argenischacon.dentalclinic.enums.Role;
import com.argenischacon.dentalclinic.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void unauthenticatedUser_ShouldBeRedirectedToLogin() throws Exception {
        mockMvc.perform(get("/"))
               .andExpect(status().isFound())
               .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    public void userWithMustChangePassword_ShouldBeRedirectedToChangePassword() throws Exception {
        User domainUser = User.builder()
                .id(1L)
                .username("testadmin")
                .password("testpass")
                .role(Role.ADMIN)
                .mustChangePassword(true)
                .active(true)
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(domainUser);

        // Al intentar acceder a un recurso protegido genérico, el Custom Filter debe interceptar y redirigir
        mockMvc.perform(get("/").with(user(customUserDetails)))
               .andExpect(status().isFound())
               .andExpect(redirectedUrl("/change-password"));
    }

    @Test
    public void userWithoutMustChangePassword_ShouldPassFilter() throws Exception {
        User domainUser = User.builder()
                .id(2L)
                .username("testdentist")
                .password("testpass")
                .role(Role.DENTIST)
                .mustChangePassword(false)
                .active(true)
                .build();
        CustomUserDetails customUserDetails = new CustomUserDetails(domainUser);

        // Al usar un usuario "sano", el MustChangePasswordFilter lo deja pasar a la cadena real.
        // No debe redirigir a /login (isFound/302) ni lanzar Access Denied (isForbidden/403).
        // Si el controlador existe y la vista carga, devuelve 200 OK.
        mockMvc.perform(get("/dentist/dashboard").with(user(customUserDetails)))
               .andExpect(status().isOk());
    }
}
