package com.argenischacon.dentalclinic.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    /** Read from spring.h2.console.enabled — true only in dev profile */
    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Value("${spring.h2.console.path:/h2-console}")
    private String h2ConsolePath;

    @Bean
    public MustChangePasswordFilter mustChangePasswordFilter() {
        return new MustChangePasswordFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           MustChangePasswordFilter mustChangePasswordFilter) throws Exception {

        // CSRF: enabled by default; ignored only for H2 console when active
        http.csrf(csrf -> {
            if (h2ConsoleEnabled) {
                csrf.ignoringRequestMatchers(h2ConsolePath + "/**");
            }
        });

        // X-Frame-Options: disabled only when H2 console is active (requires iframes)
        if (h2ConsoleEnabled) {
            http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        }

        http
            .authorizeHttpRequests(authz -> {
                // H2 Console (only in development, controlled by property)
                if (h2ConsoleEnabled) {
                    authz.requestMatchers(h2ConsolePath, h2ConsolePath + "/**").permitAll();
                }
                authz
                    // Static resources
                    .requestMatchers("/css/**", "/js/**", "/img/**", "/images/**",
                                     "/vendor/**", "/scss/**", "/webjars/**").permitAll()

                    // Authentication
                    .requestMatchers("/login", "/error").permitAll()
                    .requestMatchers("/change-password").authenticated()

                    // Roles
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .requestMatchers("/dentist/**").hasRole("DENTIST")
                    .requestMatchers("/receptionist/**").hasRole("RECEPTIONIST")

                    // General rule
                    .anyRequest().authenticated();
            })
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(successHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error/403")
            )
            .addFilterAfter(mustChangePasswordFilter, AuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
