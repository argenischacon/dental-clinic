package com.argenischacon.dentalclinic.integration.admin;

import com.argenischacon.dentalclinic.dto.treatment.TreatmentRequestDto;
import com.argenischacon.dentalclinic.service.TreatmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.argenischacon.dentalclinic.repository.TreatmentRepository;
import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminTreatmentIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TreatmentService treatmentService;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListServices() throws Exception {
        mockMvc.perform(get("/admin/treatments/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/list"))
                .andExpect(model().attributeExists("treatmentsPage", "totalTreatments", "activeTreatments", "inactiveTreatments"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAddTreatmentForm() throws Exception {
        mockMvc.perform(get("/admin/treatments/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/add"))
                .andExpect(model().attributeExists("treatmentRequestDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldSaveTreatmentSuccessfully() throws Exception {
        mockMvc.perform(post("/admin/treatments/save")
                        .with(csrf())
                        .param("treatmentCode", "SRV001")
                        .param("name", "Limpieza Dental")
                        .param("description", "Limpieza profunda con ultrasonido")
                        .param("durationMinutes", "30")
                        .param("price", "50.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/treatments/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFailToSaveTreatmentWithInvalidData() throws Exception {
        mockMvc.perform(post("/admin/treatments/save")
                        .with(csrf())
                        .param("treatmentCode", "") // Invalid
                        .param("name", "Limpieza Dental"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/add"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldEditTreatment() throws Exception {
        // Setup
        TreatmentRequestDto dto = TreatmentRequestDto.builder()
                .treatmentCode("SRV002")
                .name("Blanqueamiento")
                .description("Blanqueamiento láser")
                .durationMinutes(45)
                .price(new BigDecimal("150.00"))
                .build();
        treatmentService.treatmentAdd(dto);

        Long id = treatmentRepository.findByTreatmentCode("SRV002").orElseThrow().getId();

        mockMvc.perform(get("/admin/treatments/edit/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/treatments/edit"))
                .andExpect(model().attributeExists("treatmentRequestDto", "treatmentId"));

        mockMvc.perform(post("/admin/treatments/edit/" + id)
                        .with(csrf())
                        .param("treatmentCode", "SRV002")
                        .param("name", "Blanqueamiento Avanzado")
                        .param("description", "Blanqueamiento láser")
                        .param("durationMinutes", "60")
                        .param("price", "200.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/treatments/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateAndActivateTreatment() throws Exception {
        // Setup
        TreatmentRequestDto dto = TreatmentRequestDto.builder()
                .treatmentCode("SRV003")
                .name("Extracción")
                .description("Extracción simple")
                .durationMinutes(30)
                .price(new BigDecimal("80.00"))
                .build();
        treatmentService.treatmentAdd(dto);

        Long id = treatmentRepository.findByTreatmentCode("SRV003").orElseThrow().getId();

        mockMvc.perform(post("/admin/treatments/deactivate/" + id).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/treatments/list"))
                .andExpect(flash().attributeExists("success"));

        mockMvc.perform(post("/admin/treatments/activate/" + id).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/treatments/list"))
                .andExpect(flash().attributeExists("success"));
    }
}
