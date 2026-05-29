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
public class AdminScheduleIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DentistService dentistService;
    
    @Autowired
    private DentistRepository dentistRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAssignSchedulePage() throws Exception {
        mockMvc.perform(get("/admin/schedules/assign"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/assign"))
                .andExpect(model().attributeExists("scheduleForm", "dentists"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowAssignScheduleForSpecificDentist() throws Exception {
        // Setup dentist
        DentistRequestDto dto = DentistRequestDto.builder()
                .dni("55667788")
                .name("Roberto")
                .lastName("Gomez")
                .email("roberto.gomez@test.com")
                .phone("5555566")
                .address("Calle Principal 45")
                .birthDate(LocalDate.now().minusYears(40))
                .licenseNumber("LIC55667")
                .specialty(DentalSpecialty.ENDODONTICS)
                .hireDate(LocalDate.now().minusDays(3))
                .build();
        dentistService.dentistAdd(dto);

        Long id = dentistRepository.findByDni("55667788").orElseThrow().getId();

        mockMvc.perform(get("/admin/schedules/assign/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/assign"))
                .andExpect(model().attributeExists("scheduleForm"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFailToAssignScheduleForInvalidDentist() throws Exception {
        mockMvc.perform(get("/admin/schedules/assign/999"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/schedules/assign"))
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowViewSchedulePage() throws Exception {
        mockMvc.perform(get("/admin/schedules/view"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/view"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowViewScheduleForSpecificDentist() throws Exception {
        // Setup dentist
        DentistRequestDto dto = DentistRequestDto.builder()
                .dni("11221122")
                .name("Sofia")
                .lastName("Ruiz")
                .email("sofia.ruiz@test.com")
                .phone("5551122")
                .address("Avenida Norte 10")
                .birthDate(LocalDate.now().minusYears(32))
                .licenseNumber("LIC11221")
                .specialty(DentalSpecialty.GENERAL)
                .hireDate(LocalDate.now().minusDays(5))
                .build();
        dentistService.dentistAdd(dto);

        Long id = dentistRepository.findByDni("11221122").orElseThrow().getId();

        mockMvc.perform(get("/admin/schedules/view/" + id))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/view"))
                .andExpect(model().attributeExists("weeklySchedule", "dentistId"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldFailToSaveScheduleWithMissingData() throws Exception {
        // Setup dentist
        DentistRequestDto dto = DentistRequestDto.builder()
                .dni("99887766")
                .name("Luis")
                .lastName("Fernandez")
                .email("luis.fernandez@test.com")
                .phone("5559988")
                .address("Calle Sur 20")
                .birthDate(LocalDate.now().minusYears(45))
                .licenseNumber("LIC99887")
                .specialty(DentalSpecialty.PERIODONTICS)
                .hireDate(LocalDate.now().minusDays(1))
                .build();
        dentistService.dentistAdd(dto);

        Long id = dentistRepository.findByDni("99887766").orElseThrow().getId();

        // Assigning a schedule with missing form details will trigger a validation error or business rule exception
        mockMvc.perform(post("/admin/schedules/assign")
                        .with(csrf())
                        .param("dentistId", id.toString())
                        .param("slotDurationMinutes", "0")) // Invalid duration to ensure failure
                .andExpect(status().isOk())
                .andExpect(view().name("admin/schedules/assign"))
                .andExpect(model().attributeExists("error"));
    }
}
