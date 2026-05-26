package com.argenischacon.dentalclinic.slice.web.receptionist;

import com.argenischacon.dentalclinic.controller.receptionist.ReceptionistDashboardController;
import com.argenischacon.dentalclinic.security.CustomAuthenticationSuccessHandler;
import com.argenischacon.dentalclinic.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReceptionistDashboardController.class)
@Import(SecurityConfig.class)
public class ReceptionistDashboardControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomAuthenticationSuccessHandler successHandler;

    @Test
    @WithMockUser(roles = "RECEPTIONIST")
    public void testReceptionistAccessWithReceptionistRole() throws Exception {
        mockMvc.perform(get("/receptionist/dashboard"))
               .andExpect(status().isOk())
               .andExpect(view().name("receptionist/dashboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testReceptionistAccessWithAdminRole() throws Exception {
        // Even Admin doesn't have RECEPTIONIST role directly unless mapped
        mockMvc.perform(get("/receptionist/dashboard"))
               .andExpect(status().isForbidden());
    }

    @Test
    public void testReceptionistAccessUnauthenticated() throws Exception {
        mockMvc.perform(get("/receptionist/dashboard"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("**/login"));
    }
}
