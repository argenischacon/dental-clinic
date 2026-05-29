package com.argenischacon.dentalclinic.integration.admin;

import com.argenischacon.dentalclinic.dto.service.ServiceRequestDto;
import com.argenischacon.dentalclinic.service.ServiceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.argenischacon.dentalclinic.repository.ServiceRepository;
import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminServiceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListServices() throws Exception {
        mockMvc.perform(get("/admin/services/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/list"))
                .andExpect(model().attributeExists("servicesPage", "totalServices", "activeServices", "inactiveServices"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAddServiceForm() throws Exception {
        mockMvc.perform(get("/admin/services/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/add"))
                .andExpect(model().attributeExists("serviceRequestDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldSaveServiceSuccessfully() throws Exception {
        mockMvc.perform(post("/admin/services/save")
                        .with(csrf())
                        .param("serviceCode", "SRV001")
                        .param("name", "Limpieza Dental")
                        .param("description", "Limpieza profunda con ultrasonido")
                        .param("durationMinutes", "30")
                        .param("price", "50.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFailToSaveServiceWithInvalidData() throws Exception {
        mockMvc.perform(post("/admin/services/save")
                        .with(csrf())
                        .param("serviceCode", "") // Invalid
                        .param("name", "Limpieza Dental"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/add"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldEditService() throws Exception {
        // Setup
        ServiceRequestDto dto = ServiceRequestDto.builder()
                .serviceCode("SRV002")
                .name("Blanqueamiento")
                .description("Blanqueamiento láser")
                .durationMinutes(45)
                .price(new BigDecimal("150.00"))
                .build();
        serviceService.serviceAdd(dto);

        Long id = serviceRepository.findByServiceCode("SRV002").orElseThrow().getId();

        mockMvc.perform(get("/admin/services/edit/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/services/edit"))
                .andExpect(model().attributeExists("serviceRequestDto", "serviceId"));

        mockMvc.perform(post("/admin/services/edit/" + id)
                        .with(csrf())
                        .param("serviceCode", "SRV002")
                        .param("name", "Blanqueamiento Avanzado")
                        .param("description", "Blanqueamiento láser")
                        .param("durationMinutes", "60")
                        .param("price", "200.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateAndActivateService() throws Exception {
        // Setup
        ServiceRequestDto dto = ServiceRequestDto.builder()
                .serviceCode("SRV003")
                .name("Extracción")
                .description("Extracción simple")
                .durationMinutes(30)
                .price(new BigDecimal("80.00"))
                .build();
        serviceService.serviceAdd(dto);

        Long id = serviceRepository.findByServiceCode("SRV003").orElseThrow().getId();

        mockMvc.perform(post("/admin/services/deactivate/" + id).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services/list"))
                .andExpect(flash().attributeExists("success"));

        mockMvc.perform(post("/admin/services/activate/" + id).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/services/list"))
                .andExpect(flash().attributeExists("success"));
    }
}
