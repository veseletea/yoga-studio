package com.yogastudio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // frontend static
                        .requestMatchers("/", "/index.html", "/assets/**", "/*.svg", "/*.ico").permitAll()

                        // documentatie API
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()

                        // asistentul AI ramane public (demo)
                        .requestMatchers("/api/assistant/**").permitAll()

                        // citirea claselor si instructorilor e publica
                        .requestMatchers(HttpMethod.GET, "/api/classes/**", "/api/instructors/**").permitAll()

                        // orice altceva cere autentificare
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {});

        return http.build();
    }
}