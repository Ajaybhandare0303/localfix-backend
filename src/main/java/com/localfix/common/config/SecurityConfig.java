package com.localfix.common.config;

import com.localfix.auth.jwt.JwtAuthenticationFilter;
import com.localfix.auth.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // ============================================================
    // Password Encoder
    // ============================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ============================================================
    // Authentication Provider
    // ============================================================

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        provider.setUserDetailsService(
                customUserDetailsService
        );

        provider.setPasswordEncoder(
                passwordEncoder()
        );

        return provider;
    }

    // ============================================================
    // Authentication Manager
    // ============================================================

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    // ============================================================
    // Security Filter Chain
    // ============================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

                // ------------------------------------------------
                // CSRF
                // ------------------------------------------------
                .csrf(csrf -> csrf.disable())

                // ------------------------------------------------
                // Disable default authentication mechanisms
                // ------------------------------------------------
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable())

                // ------------------------------------------------
                // Authentication Provider
                // ------------------------------------------------
                .authenticationProvider(
                        authenticationProvider()
                )

                // ------------------------------------------------
                // Authorization Rules
                // ------------------------------------------------
                .authorizeHttpRequests(auth -> auth

                        // ================================
                        // Authentication APIs
                        // ================================
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/api/v1/auth/forgot-password",
                                "/api/v1/auth/verify-reset-otp",
                                "/api/v1/auth/reset-password"
                        ).permitAll()

                        // ================================
                        // Swagger
                        // ================================
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // ================================
                        // Actuator
                        // ================================
                        .requestMatchers(
                                "/actuator/health"
                        ).permitAll()

                        // ================================
                        // Everything else
                        // ================================
                        .anyRequest().authenticated()
                )

                // ------------------------------------------------
                // CORS
                // ------------------------------------------------
                .cors(Customizer.withDefaults())

                // ------------------------------------------------
                // Security Headers
                // ------------------------------------------------
                .headers(headers -> headers
                        .frameOptions(frame ->
                                frame.deny()
                        )
                        .contentTypeOptions(
                                Customizer.withDefaults()
                        )
                )

                // ------------------------------------------------
                // JWT Filter
                // ------------------------------------------------
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}