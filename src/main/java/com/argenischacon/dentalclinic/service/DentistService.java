package com.argenischacon.dentalclinic.service;

import com.argenischacon.dentalclinic.dto.dentist.DentistListDto;
import com.argenischacon.dentalclinic.dto.dentist.DentistRequestDto;
import com.argenischacon.dentalclinic.enums.Role;
import com.argenischacon.dentalclinic.mappers.DentistMapper;
import com.argenischacon.dentalclinic.model.Dentist;
import com.argenischacon.dentalclinic.model.User;
import com.argenischacon.dentalclinic.repository.DentistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DentistService {
    private final DentistRepository dentistRepository;
    private final DentistMapper dentistMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void dentistAdd(DentistRequestDto dentistRequestDto) {
        Dentist dentist = dentistMapper.toEntity(dentistRequestDto);

        User newUser = User.builder()
                .username(dentist.getDni())
                .password(passwordEncoder.encode(dentist.getDni()))
                .role(Role.DENTIST)
                .build();

        dentist.setUser(newUser);
        dentistRepository.save(dentist);
    }

    public Page<DentistListDto> findAllDentists(Pageable pageable) {
        return dentistRepository.findAll(pageable).map(dentistMapper::toListDto);
    }
}
