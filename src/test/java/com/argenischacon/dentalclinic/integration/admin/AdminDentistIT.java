package com.argenischacon.dentalclinic.integration.admin;

import com.argenischacon.dentalclinic.dto.dentist.DentistRequestDto;
import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import com.argenischacon.dentalclinic.service.DentistService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.argenischacon.dentalclinic.repository.DentistRepository;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminDentistIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DentistService dentistService;
    
    @Autowired
    private DentistRepository dentistRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldListDentists() throws Exception {
        mockMvc.perform(get("/admin/dentists/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dentists/list"))
                .andExpect(model().attributeExists("dentistsPage", "totalDentists", "activeDentists", "inactiveDentists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAddDentistForm() throws Exception {
        mockMvc.perform(get("/admin/dentists/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dentists/add"))
                .andExpect(model().attributeExists("dentistRequestDto"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldSaveDentistSuccessfully() throws Exception {
        mockMvc.perform(post("/admin/dentists/save")
                        .with(csrf())
                        .param("dni", "123456789")
                        .param("name", "Juan")
                        .param("lastName", "Pérez")
                        .param("email", "juan.perez@test.com")
                        .param("phone", "55512345")
                        .param("address", "Calle Falsa 123")
                        .param("birthDate", LocalDate.now().minusYears(30).toString())
                        .param("licenseNumber", "LIC98765")
                        .param("specialty", DentalSpecialty.GENERAL.name())
                        .param("hireDate", LocalDate.now().minusDays(1).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dentists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFailToSaveDentistWithInvalidData() throws Exception {
        mockMvc.perform(post("/admin/dentists/save")
                        .with(csrf())
                        .param("dni", "") // Invalid DNI
                        .param("name", "Juan"))
                .andExpect(status().isOk()) // returns to form
                .andExpect(view().name("admin/dentists/add"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldEditDentist() throws Exception {
        // Prepare data by saving a dentist via service
        DentistRequestDto dto = DentistRequestDto.builder()
                .dni("987654321")
                .name("Maria")
                .lastName("Gomez")
                .email("maria.gomez@test.com")
                .phone("55598765")
                .address("Avenida Siempre Viva 742")
                .birthDate(LocalDate.now().minusYears(28))
                .licenseNumber("LIC12345")
                .specialty(DentalSpecialty.ORTHODONTICS)
                .hireDate(LocalDate.now().minusDays(5))
                .build();
        dentistService.dentistAdd(dto);
        // Find the id via repository
        Long id = dentistRepository.findByDni("987654321").orElseThrow().getId();

        mockMvc.perform(get("/admin/dentists/edit/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dentists/edit"))
                .andExpect(model().attributeExists("dentistRequestDto", "dentistId"));

        mockMvc.perform(post("/admin/dentists/edit/" + id)
                        .with(csrf())
                        .param("dni", "987654321")
                        .param("name", "Maria Modificada")
                        .param("lastName", "Gomez")
                        .param("email", "maria.gomez@test.com")
                        .param("phone", "55598765")
                        .param("address", "Avenida Siempre Viva 742")
                        .param("birthDate", LocalDate.now().minusYears(28).toString())
                        .param("licenseNumber", "LIC12345")
                        .param("specialty", DentalSpecialty.ORTHODONTICS.name())
                        .param("hireDate", LocalDate.now().minusDays(5).toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dentists/list"))
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateAndActivateDentist() throws Exception {
        // Setup
        DentistRequestDto dto = DentistRequestDto.builder()
                .dni("11223344")
                .name("Pedro")
                .lastName("Sanchez")
                .email("pedro.sanchez@test.com")
                .phone("55511223")
                .address("Calle 8")
                .birthDate(LocalDate.now().minusYears(35))
                .licenseNumber("LIC11223")
                .specialty(DentalSpecialty.PEDIATRIC)
                .hireDate(LocalDate.now().minusDays(10))
                .build();
        dentistService.dentistAdd(dto);
        Long id = dentistRepository.findByDni("11223344").orElseThrow().getId();

        // Deactivate
        mockMvc.perform(post("/admin/dentists/deactivate/" + id).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dentists/list"))
                .andExpect(flash().attributeExists("success"));

        // Activate
        mockMvc.perform(post("/admin/dentists/activate/" + id).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dentists/list"))
                .andExpect(flash().attributeExists("success"));
    }

}
