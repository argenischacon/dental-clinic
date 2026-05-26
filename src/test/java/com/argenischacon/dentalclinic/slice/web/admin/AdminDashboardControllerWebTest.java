package com.argenischacon.dentalclinic.slice.web.admin;

import com.argenischacon.dentalclinic.controller.admin.AdminDashboardController;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@WebMvcTest(AdminDashboardController.class)
@Import(SecurityConfig.class)
public class AdminDashboardControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomAuthenticationSuccessHandler successHandler;

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminAccessWithAdminRole() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
               .andDo(print())
               .andExpect(status().isOk())
               .andExpect(view().name("admin/dashboard"));
    }

    @Test
    @WithMockUser(roles = "DENTIST")
    public void testAdminAccessWithDentistRole() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
               .andExpect(status().isForbidden());
    }

    @Test
    public void testAdminAccessUnauthenticated() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
               .andExpect(status().is3xxRedirection())
               .andExpect(redirectedUrlPattern("**/login"));
    }
}
