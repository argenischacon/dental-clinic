package com.argenischacon.dentalclinic.security;

import com.argenischacon.dentalclinic.model.User;
import com.argenischacon.dentalclinic.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado con username: " + username);
        }

        User user = optionalUser.get();
        if (!user.isActive()) {
            throw new UsernameNotFoundException("La cuenta del usuario " + username + " está desactivada");
        }

        return new CustomUserDetails(user);
    }
}
