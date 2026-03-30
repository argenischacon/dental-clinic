package com.argenischacon.dentalclinic.slice.web;

import com.argenischacon.dentalclinic.controller.DentistController;
import com.argenischacon.dentalclinic.security.CustomAuthenticationSuccessHandler;
import com.argenischacon.dentalclinic.security.MustChangePasswordFilter;
import com.argenischacon.dentalclinic.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DentistController.class)
@Import(SecurityConfig.class)
public class DentistControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomAuthenticationSuccessHandler successHandler;

    @MockitoBean
    private MustChangePasswordFilter mustChangePasswordFilter;

    @Test
    @WithMockUser(roles = "DENTIST")
    public void testDentistAccessWithDentistRole() throws Exception {
        mockMvc.perform(get("/dentist/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("dentist/dashboard"));
    }

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    public void testDentistAccessWithReceptionistRole() throws Exception {
        mockMvc.perform(get("/dentist/dashboard"))
               .andExpect(status().isForbidden());
    }

    @Test
    public void testDentistAccessUnauthenticated() throws Exception {
        mockMvc.perform(get("/dentist/dashboard"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("**/login"));
    }
}
