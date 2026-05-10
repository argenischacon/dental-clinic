package com.argenischacon.dentalclinic.slice.web;

import com.argenischacon.dentalclinic.controller.AdminController;
import com.argenischacon.dentalclinic.dto.dentist.DentistListDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistRequestDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistResponseDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistStatsDto;
import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.security.CustomAuthenticationSuccessHandler;
import com.argenischacon.dentalclinic.security.SecurityConfig;
import com.argenischacon.dentalclinic.service.DentistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
public class AdminDentistControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DentistService dentistService;

    @MockBean
    private CustomAuthenticationSuccessHandler successHandler;

    private DentistRequestDto defaultRequestDto;

    @BeforeEach
    void setUp() {
        defaultRequestDto = new DentistRequestDto(
                "12345678", "John", "Doe", "john.doe@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "LIC-123",
                DentalSpecialty.GENERAL, LocalDate.of(2020, 1, 1)
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDentistListPage() throws Exception {
        DentistStatsDto statsDto = new DentistStatsDto(10L, 8L, 2L);
        DentistListDto dentistListDto = new DentistListDto(
                1L, "John", "Doe", "LIC-123", DentalSpecialty.GENERAL,
                "john@example.com", "123456789", true
        );
        Page<DentistListDto> page = new PageImpl<>(List.of(dentistListDto));

        when(dentistService.getStats()).thenReturn(statsDto);
        when(dentistService.findAllDentists(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/dentists/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dentists/list"))
                .andExpect(model().attributeExists("dentistsPage"))
                .andExpect(model().attributeExists("totalDentists"))
                .andExpect(model().attribute("activeDentists", 8L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDentistAddPage() throws Exception {
        mockMvc.perform(get("/admin/dentists/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dentists/add"))
                .andExpect(model().attributeExists("dentistRequestDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveDentist_Success() throws Exception {
        doNothing().when(dentistService).dentistAdd(any(DentistRequestDto.class));

        mockMvc.perform(post("/admin/dentists/save")
                        .with(csrf())
                        .param("name", "John")
                        .param("lastName", "Doe")
                        .param("dni", "12345678")
                        .param("licenseNumber", "LIC-123")
                        .param("specialty", "GENERAL")
                        .param("email", "john@example.com")
                        .param("phone", "123456789")
                        .param("address", "Av. Siempre Viva 123")
                        .param("birthDate", "1990-01-01")
                        .param("hireDate", "2020-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dentists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveDentist_ValidationErrors() throws Exception {
        mockMvc.perform(post("/admin/dentists/save")
                        .with(csrf())
                        // Omit mandatory fields like name and dni to trigger validation errors
                        .param("name", ""))
                .andExpect(status().isOk()) // returns the form with errors
                .andExpect(view().name("admin/dentists/add"))
                .andExpect(model().attributeHasFieldErrors("dentistRequestDto", "name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveDentist_BusinessRuleException() throws Exception {
        doThrow(new BusinessRuleException("DNI duplicado")).when(dentistService).dentistAdd(any(DentistRequestDto.class));

        mockMvc.perform(post("/admin/dentists/save")
                        .with(csrf())
                        .param("name", "John")
                        .param("lastName", "Doe")
                        .param("dni", "12345678")
                        .param("licenseNumber", "LIC-123")
                        .param("specialty", "GENERAL")
                        .param("email", "john@example.com")
                        .param("phone", "123456789")
                        .param("address", "Av. Siempre Viva 123")
                        .param("birthDate", "1990-01-01")
                        .param("hireDate", "2020-01-01"))
                .andExpect(status().isOk()) // returns the form
                .andExpect(view().name("admin/dentists/add"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetDentistDetailModal() throws Exception {
        DentistResponseDto responseDto = new DentistResponseDto(
                1L, "12345678", "John", "Doe", "john@example.com", "123456789",
                "Av. Siempreviva 123", "12345678", LocalDate.of(1990, 1, 1),
                "LIC-123", DentalSpecialty.GENERAL, LocalDate.of(2020, 1, 1), true
        );
        when(dentistService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/admin/dentists/1/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dentists/_detail :: modal-content"))
                .andExpect(model().attributeExists("dentist"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDentistEditPage() throws Exception {
        when(dentistService.getDentistForEdit(1L)).thenReturn(defaultRequestDto);

        mockMvc.perform(get("/admin/dentists/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dentists/edit"))
                .andExpect(model().attributeExists("dentistRequestDto"))
                .andExpect(model().attributeExists("dentistId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateDentist_Success() throws Exception {
        doNothing().when(dentistService).updateDentist(eq(1L), any(DentistRequestDto.class));

        mockMvc.perform(post("/admin/dentists/edit/1")
                        .with(csrf())
                        .param("name", "John")
                        .param("lastName", "Doe")
                        .param("dni", "12345678")
                        .param("licenseNumber", "LIC-123")
                        .param("specialty", "GENERAL")
                        .param("email", "john@example.com")
                        .param("phone", "123456789")
                        .param("address", "Av. Siempre Viva 123")
                        .param("birthDate", "1990-01-01")
                        .param("hireDate", "2020-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dentists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActivateDentist() throws Exception {
        doNothing().when(dentistService).activateDentist(1L);

        mockMvc.perform(post("/admin/dentists/activate/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dentists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeactivateDentist() throws Exception {
        doNothing().when(dentistService).deactivateDentist(1L);

        mockMvc.perform(post("/admin/dentists/deactivate/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dentists/list"))
                .andExpect(flash().attributeExists("success"));
    }

}
