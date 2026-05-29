package com.argenischacon.dentalclinic.slice.web.admin;

import com.argenischacon.dentalclinic.controller.admin.AdminReceptionistController;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistListDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistRequestDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistResponseDto;
import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistStatsDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.security.CustomAuthenticationSuccessHandler;
import com.argenischacon.dentalclinic.security.SecurityConfig;
import com.argenischacon.dentalclinic.service.ReceptionistService;
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

@WebMvcTest(AdminReceptionistController.class)
@Import(SecurityConfig.class)
public class AdminReceptionistControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReceptionistService receptionistService;

    @MockBean
    private CustomAuthenticationSuccessHandler successHandler;

    private ReceptionistRequestDto defaultRequestDto;

    @BeforeEach
    void setUp() {
        defaultRequestDto = new ReceptionistRequestDto(
                "12345678", "Maria", "Gomez", "maria.gomez@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "EMP-001", LocalDate.of(2020, 1, 1)
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testReceptionistListPage() throws Exception {
        ReceptionistStatsDto statsDto = new ReceptionistStatsDto(10L, 8L, 2L);
        ReceptionistListDto receptionistListDto = new ReceptionistListDto(
                1L, "EMP-001", "12345678", "Maria", "Gomez",
                "maria@example.com", "123456789", true
        );
        Page<ReceptionistListDto> page = new PageImpl<>(List.of(receptionistListDto));

        when(receptionistService.getStats()).thenReturn(statsDto);
        when(receptionistService.findAllReceptionists(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/receptionists/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/receptionists/list"))
                .andExpect(model().attributeExists("receptionistsPage"))
                .andExpect(model().attributeExists("totalReceptionists"))
                .andExpect(model().attribute("activeReceptionists", 8L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testReceptionistAddPage() throws Exception {
        mockMvc.perform(get("/admin/receptionists/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/receptionists/add"))
                .andExpect(model().attributeExists("receptionistRequestDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveReceptionist_Success() throws Exception {
        doNothing().when(receptionistService).receptionistAdd(any(ReceptionistRequestDto.class));

        mockMvc.perform(post("/admin/receptionists/save")
                        .with(csrf())
                        .param("name", "Maria")
                        .param("lastName", "Gomez")
                        .param("dni", "12345678")
                        .param("employeeNumber", "EMP-001")
                        .param("email", "maria@example.com")
                        .param("phone", "123456789")
                        .param("address", "Av. Siempre Viva 123")
                        .param("birthDate", "1990-01-01")
                        .param("hireDate", "2020-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/receptionists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveReceptionist_ValidationErrors() throws Exception {
        mockMvc.perform(post("/admin/receptionists/save")
                        .with(csrf())
                        // Omit mandatory fields like name and dni to trigger validation errors
                        .param("name", ""))
                .andExpect(status().isOk()) // returns the form with errors
                .andExpect(view().name("admin/receptionists/add"))
                .andExpect(model().attributeHasFieldErrors("receptionistRequestDto", "name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveReceptionist_BusinessRuleException() throws Exception {
        doThrow(new BusinessRuleException("DNI duplicado")).when(receptionistService).receptionistAdd(any(ReceptionistRequestDto.class));

        mockMvc.perform(post("/admin/receptionists/save")
                        .with(csrf())
                        .param("name", "Maria")
                        .param("lastName", "Gomez")
                        .param("dni", "12345678")
                        .param("employeeNumber", "EMP-001")
                        .param("email", "maria@example.com")
                        .param("phone", "123456789")
                        .param("address", "Av. Siempre Viva 123")
                        .param("birthDate", "1990-01-01")
                        .param("hireDate", "2020-01-01"))
                .andExpect(status().isOk()) // returns the form
                .andExpect(view().name("admin/receptionists/add"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetReceptionistDetailModal() throws Exception {
        ReceptionistResponseDto responseDto = new ReceptionistResponseDto(
                1L, "12345678", "Maria", "Gomez", "maria@example.com", "123456789",
                "Av. Siempreviva 123", LocalDate.of(1990, 1, 1), "EMP-001", LocalDate.of(2020, 1, 1), "12345678", true
        );
        when(receptionistService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/admin/receptionists/1/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/receptionists/_detail :: modal-content"))
                .andExpect(model().attributeExists("receptionist"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testReceptionistEditPage() throws Exception {
        when(receptionistService.getReceptionistForEdit(1L)).thenReturn(defaultRequestDto);

        mockMvc.perform(get("/admin/receptionists/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/receptionists/edit"))
                .andExpect(model().attributeExists("receptionistRequestDto"))
                .andExpect(model().attributeExists("receptionistId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateReceptionist_Success() throws Exception {
        doNothing().when(receptionistService).updateReceptionist(eq(1L), any(ReceptionistRequestDto.class));

        mockMvc.perform(post("/admin/receptionists/edit/1")
                        .with(csrf())
                        .param("name", "Maria")
                        .param("lastName", "Gomez")
                        .param("dni", "12345678")
                        .param("employeeNumber", "EMP-001")
                        .param("email", "maria@example.com")
                        .param("phone", "123456789")
                        .param("address", "Av. Siempre Viva 123")
                        .param("birthDate", "1990-01-01")
                        .param("hireDate", "2020-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/receptionists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActivateReceptionist() throws Exception {
        doNothing().when(receptionistService).activateReceptionist(1L);

        mockMvc.perform(post("/admin/receptionists/activate/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/receptionists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeactivateReceptionist() throws Exception {
        doNothing().when(receptionistService).deactivateReceptionist(1L);

        mockMvc.perform(post("/admin/receptionists/deactivate/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/receptionists/list"))
                .andExpect(flash().attributeExists("success"));
    }

}
