package com.argenischacon.dentalclinic.service;

import com.argenischacon.dentalclinic.dto.user.ChangePasswordRequest;
import com.argenischacon.dentalclinic.model.User;
import com.argenischacon.dentalclinic.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(Long userId, ChangePasswordRequest request) {
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        User user = userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado."));

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setMustChangePassword(false);
        userRepository.save(user);
    }
}
