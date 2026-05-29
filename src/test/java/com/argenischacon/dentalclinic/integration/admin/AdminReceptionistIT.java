package com.argenischacon.dentalclinic.integration.admin;

import com.argenischacon.dentalclinic.dto.receptionist.ReceptionistRequestDto;
import com.argenischacon.dentalclinic.service.ReceptionistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.argenischacon.dentalclinic.repository.ReceptionistRepository;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminReceptionistIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReceptionistService receptionistService;

    @Autowired
    private ReceptionistRepository receptionistRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListReceptionists() throws Exception {
        mockMvc.perform(get("/admin/receptionists/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/receptionists/list"))
                .andExpect(model().attributeExists("receptionistsPage", "totalReceptionists", "activeReceptionists", "inactiveReceptionists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAddReceptionistForm() throws Exception {
        mockMvc.perform(get("/admin/receptionists/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/receptionists/add"))
                .andExpect(model().attributeExists("receptionistRequestDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldSaveReceptionistSuccessfully() throws Exception {
        mockMvc.perform(post("/admin/receptionists/save")
                        .with(csrf())
                        .param("dni", "88776655")
                        .param("name", "Ana")
                        .param("lastName", "López")
                        .param("email", "ana.lopez@test.com")
                        .param("phone", "5558877")
                        .param("address", "Calle Recepción 123")
                        .param("birthDate", LocalDate.now().minusYears(25).toString())
                        .param("employeeNumber", "EMP001")
                        .param("hireDate", LocalDate.now().minusDays(1).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/receptionists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFailToSaveReceptionistWithInvalidData() throws Exception {
        mockMvc.perform(post("/admin/receptionists/save")
                        .with(csrf())
                        .param("dni", "") // Invalid DNI
                        .param("name", "Ana"))
                .andExpect(status().isOk()) // returns to form
                .andExpect(view().name("admin/receptionists/add"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldEditReceptionist() throws Exception {
        // Prepare data
        ReceptionistRequestDto dto = ReceptionistRequestDto.builder()
                .dni("12312312")
                .name("Laura")
                .lastName("García")
                .email("laura.garcia@test.com")
                .phone("55512312")
                .address("Avenida Principal 12")
                .birthDate(LocalDate.now().minusYears(30))
                .employeeNumber("EMP002")
                .hireDate(LocalDate.now().minusDays(10))
                .build();
        receptionistService.receptionistAdd(dto);

        Long id = receptionistRepository.findByDni("12312312").orElseThrow().getId();

        mockMvc.perform(get("/admin/receptionists/edit/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/receptionists/edit"))
                .andExpect(model().attributeExists("receptionistRequestDto", "receptionistId"));

        mockMvc.perform(post("/admin/receptionists/edit/" + id)
                        .with(csrf())
                        .param("dni", "12312312")
                        .param("name", "Laura Modificada")
                        .param("lastName", "García")
                        .param("email", "laura.garcia@test.com")
                        .param("phone", "55512312")
                        .param("address", "Avenida Principal 12")
                        .param("birthDate", LocalDate.now().minusYears(30).toString())
                        .param("employeeNumber", "EMP002")
                        .param("hireDate", LocalDate.now().minusDays(10).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/receptionists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateAndActivateReceptionist() throws Exception {
        // Setup
        ReceptionistRequestDto dto = ReceptionistRequestDto.builder()
                .dni("44556677")
                .name("Carlos")
                .lastName("Ruiz")
                .email("carlos.ruiz@test.com")
                .phone("5554455")
                .address("Calle Secundaria 5")
                .birthDate(LocalDate.now().minusYears(22))
                .employeeNumber("EMP003")
                .hireDate(LocalDate.now().minusDays(2))
                .build();
        receptionistService.receptionistAdd(dto);

        Long id = receptionistRepository.findByDni("44556677").orElseThrow().getId();

        // Deactivate
        mockMvc.perform(post("/admin/receptionists/deactivate/" + id).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/receptionists/list"))
                .andExpect(flash().attributeExists("success"));

        // Activate
        mockMvc.perform(post("/admin/receptionists/activate/" + id).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/receptionists/list"))
                .andExpect(flash().attributeExists("success"));
    }
}
