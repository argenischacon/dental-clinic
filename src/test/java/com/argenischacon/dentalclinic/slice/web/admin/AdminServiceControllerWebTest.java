package com.argenischacon.dentalclinic.slice.web.admin;

import com.argenischacon.dentalclinic.controller.admin.AdminServiceController;
import com.argenischacon.dentalclinic.dto.service.ServiceListDto;
import com.argenischacon.dentalclinic.dto.service.ServiceRequestDto;
import com.argenischacon.dentalclinic.dto.service.ServiceResponseDto;
import com.argenischacon.dentalclinic.dto.service.ServiceStatsDto;
import com.argenischacon.dentalclinic.exception.BusinessRuleException;
import com.argenischacon.dentalclinic.security.CustomAuthenticationSuccessHandler;
import com.argenischacon.dentalclinic.security.SecurityConfig;
import com.argenischacon.dentalclinic.service.ServiceService;
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

@WebMvcTest(AdminServiceController.class)
@Import(SecurityConfig.class)
public class AdminServiceControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceService serviceService;

    @MockBean
    private CustomAuthenticationSuccessHandler successHandler;

    private ServiceRequestDto defaultRequestDto;

    @BeforeEach
    void setUp() {
        defaultRequestDto = new ServiceRequestDto(
                "LIM-001", "Limpieza", "Limpieza Dental", 30, new BigDecimal("30.00")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testServiceListPage() throws Exception {
        ServiceStatsDto statsDto = new ServiceStatsDto(15L, 10L, 5L);
        ServiceListDto listDto = new ServiceListDto(
                1L, "LIM-001", "Limpieza", new BigDecimal("30.00"), 30, true
        );
        Page<ServiceListDto> page = new PageImpl<>(List.of(listDto));

        when(serviceService.getStats()).thenReturn(statsDto);
        when(serviceService.findAllServices(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/services/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/list"))
                .andExpect(model().attributeExists("servicesPage"))
                .andExpect(model().attribute("totalServices", 15L))
                .andExpect(model().attribute("activeServices", 10L))
                .andExpect(model().attribute("inactiveServices", 5L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testServiceAddPage() throws Exception {
        mockMvc.perform(get("/admin/services/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/add"))
                .andExpect(model().attributeExists("serviceRequestDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveService_Success() throws Exception {
        doNothing().when(serviceService).serviceAdd(any(ServiceRequestDto.class));

        mockMvc.perform(post("/admin/services/save")
                        .with(csrf())
                        .param("serviceCode", "LIM-001")
                        .param("name", "Limpieza")
                        .param("description", "Limpieza dental profunda")
                        .param("durationMinutes", "30")
                        .param("price", "30.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveService_ValidationErrors() throws Exception {
        mockMvc.perform(post("/admin/services/save")
                        .with(csrf())
                        // Omitiendo name para forzar el error
                        .param("serviceCode", "LIM-001"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/add"))
                .andExpect(model().attributeHasFieldErrors("serviceRequestDto", "name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testSaveService_BusinessRuleException() throws Exception {
        doThrow(new BusinessRuleException("El código de servicio ya existe")).when(serviceService).serviceAdd(any(ServiceRequestDto.class));

        mockMvc.perform(post("/admin/services/save")
                        .with(csrf())
                        .param("serviceCode", "LIM-001")
                        .param("name", "Limpieza")
                        .param("description", "Limpieza dental profunda")
                        .param("durationMinutes", "30")
                        .param("price", "30.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/add"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetServiceDetailModal() throws Exception {
        ServiceResponseDto responseDto = new ServiceResponseDto(
                1L, "LIM-001", "Limpieza", "Limpieza", 30, new BigDecimal("30.00"), true, LocalDateTime.now(), LocalDateTime.now()
        );
        when(serviceService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(get("/admin/services/1/detail"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/_detail :: modal-content"))
                .andExpect(model().attributeExists("service"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testServiceEditPage() throws Exception {
        when(serviceService.getServiceForEdit(1L)).thenReturn(defaultRequestDto);

        mockMvc.perform(get("/admin/services/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/edit"))
                .andExpect(model().attributeExists("serviceRequestDto"))
                .andExpect(model().attributeExists("serviceId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateService_Success() throws Exception {
        doNothing().when(serviceService).updateService(eq(1L), any(ServiceRequestDto.class));

        mockMvc.perform(post("/admin/services/edit/1")
                        .with(csrf())
                        .param("serviceCode", "LIM-001")
                        .param("name", "Limpieza Editada")
                        .param("description", "Limpieza dental")
                        .param("durationMinutes", "30")
                        .param("price", "30.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateService_BusinessRuleException() throws Exception {
        doThrow(new BusinessRuleException("El nombre del servicio ya está en uso")).when(serviceService).updateService(eq(1L), any(ServiceRequestDto.class));

        mockMvc.perform(post("/admin/services/edit/1")
                        .with(csrf())
                        .param("serviceCode", "LIM-001")
                        .param("name", "Limpieza Editada")
                        .param("description", "Limpieza dental")
                        .param("durationMinutes", "30")
                        .param("price", "30.00"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/edit"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testActivateService() throws Exception {
        doNothing().when(serviceService).activateService(1L);

        mockMvc.perform(post("/admin/services/activate/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeactivateService() throws Exception {
        doNothing().when(serviceService).deactivateService(1L);

        mockMvc.perform(post("/admin/services/deactivate/1").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services/list"))
                .andExpect(flash().attributeExists("success"));
    }
}
