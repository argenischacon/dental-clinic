package com.argenischacon.dentalclinic.slice.web.admin;

import com.argenischacon.dentalclinic.controller.admin.AdminTreatmentController;
import com.argenischacon.dentalclinic.dto.treatment.*;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.security.CustomAuthenticationSuccessHandler;
import com.argenischacon.dentalclinic.security.SecurityConfig;
import com.argenischacon.dentalclinic.service.TreatmentService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

@WebMvcTest(AdminTreatmentController.class)
@Import(SecurityConfig.class)
public class AdminTreatmentControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TreatmentService treatmentService;

    @MockBean
    private CustomAuthenticationSuccessHandler successHandler;

    private TreatmentRequestDto defaultRequestDto;

    @BeforeEach
    void setUp() {
        defaultRequestDto = new TreatmentRequestDto(
                "LIM-001", "Limpieza", "Limpieza Dental", 30, new BigDecimal("30.00")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testTreatmentListPage() throws Exception {
        TreatmentStatsDto statsDto = new TreatmentStatsDto(15L, 10L, 5L);
        TreatmentListDto listDto = new TreatmentListDto(
                1L, "LIM-001", "Limpieza", new BigDecimal("30.00"), 30, true
        );
        Page<TreatmentListDto> page = new PageImpl<>(List.of(listDto));

        when(treatmentService.getStats()).thenReturn(statsDto);
        when(treatmentService.findAllTreatments(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/treatments/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/list"))
                .andExpect(model().attributeExists("treatmentsPage"))
                .andExpect(model().attribute("totalTreatments", 15L))
                .andExpect(model().attribute("activeTreatments", 10L))
                .andExpect(model().attribute("inactiveTreatments", 5L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testTreatmentAddPage() throws Exception {
        mockMvc.perform(get("/admin/treatments/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/add"))
                .andExpect(model().attributeExists("treatmentRequestDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveTreatment_Success() throws Exception {
        doNothing().when(treatmentService).treatmentAdd(any(TreatmentRequestDto.class));

        mockMvc.perform(post("/admin/treatments/save")
                        .with(csrf())
                        .param("treatmentCode", "LIM-001")
                        .param("name", "Limpieza")
                        .param("description", "Limpieza dental profunda")
                        .param("durationMinutes", "30")
                        .param("price", "30.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/treatments/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveTreatment_ValidationErrors() throws Exception {
        mockMvc.perform(post("/admin/treatments/save")
                        .with(csrf())
                        // Omitiendo name para forzar el error
                        .param("treatmentCode", "LIM-001"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/add"))
                .andExpect(model().attributeHasFieldErrors("treatmentRequestDto", "name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveTreatment_BusinessRuleException() throws Exception {
        doThrow(new BusinessRuleException("El código de tratamiento ya existe")).when(treatmentService).treatmentAdd(any(TreatmentRequestDto.class));

        mockMvc.perform(post("/admin/treatments/save")
                        .with(csrf())
                        .param("treatmentCode", "LIM-001")
                        .param("name", "Limpieza")
                        .param("description", "Limpieza dental profunda")
                        .param("durationMinutes", "30")
                        .param("price", "30.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/add"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetTreatmentDetailModal() throws Exception {
        TreatmentResponseDto responseDto = new TreatmentResponseDto(
                1L, "LIM-001", "Limpieza", "Limpieza", 30, new BigDecimal("30.00"), true, LocalDateTime.now(), LocalDateTime.now()
        );
        when(treatmentService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/admin/treatments/1/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/_detail :: modal-content"))
                .andExpect(model().attributeExists("treatment"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testTreatmentEditPage() throws Exception {
        when(treatmentService.getTreatmentForEdit(1L)).thenReturn(defaultRequestDto);

        mockMvc.perform(get("/admin/treatments/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/edit"))
                .andExpect(model().attributeExists("treatmentRequestDto"))
                .andExpect(model().attributeExists("treatmentId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTreatment_Success() throws Exception {
        doNothing().when(treatmentService).updateTreatment(eq(1L), any(TreatmentRequestDto.class));

        mockMvc.perform(post("/admin/treatments/edit/1")
                        .with(csrf())
                        .param("treatmentCode", "LIM-001")
                        .param("name", "Limpieza Editada")
                        .param("description", "Limpieza dental")
                        .param("durationMinutes", "30")
                        .param("price", "30.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/treatments/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateTreatment_BusinessRuleException() throws Exception {
        doThrow(new BusinessRuleException("El nombre del tratamiento ya está en uso")).when(treatmentService).updateTreatment(eq(1L), any(TreatmentRequestDto.class));

        mockMvc.perform(post("/admin/treatments/edit/1")
                        .with(csrf())
                        .param("treatmentCode", "LIM-001")
                        .param("name", "Limpieza Editada")
                        .param("description", "Limpieza dental")
                        .param("durationMinutes", "30")
                        .param("price", "30.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/edit"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActivateTreatment() throws Exception {
        doNothing().when(treatmentService).activateTreatment(1L);

        mockMvc.perform(post("/admin/treatments/activate/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/treatments/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeactivateTreatment() throws Exception {
        doNothing().when(treatmentService).deactivateTreatment(1L);

        mockMvc.perform(post("/admin/treatments/deactivate/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/treatments/list"))
                .andExpect(flash().attributeExists("success"));
    }
}
