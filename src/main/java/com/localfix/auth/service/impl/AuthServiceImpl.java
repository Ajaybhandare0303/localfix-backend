package com.localfix.auth.service.impl;

import com.localfix.auth.dto.request.LoginRequest;
import com.localfix.auth.dto.request.RegisterRequest;
import com.localfix.auth.dto.response.LoginResponse;
import com.localfix.auth.dto.response.RegisterResponse;
import com.localfix.auth.jwt.JwtService;
import com.localfix.auth.security.CustomUserDetails;
import com.localfix.auth.service.AuthService;
import com.localfix.common.enums.AccountStatus;
import com.localfix.common.enums.RoleType;
import com.localfix.common.exception.ResourceAlreadyExistsException;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;
import com.localfix.user.role.entity.Role;
import com.localfix.user.role.repository.RoleRepository;
import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    @Override
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            log.error("======================Email already registered======================");
            throw new ResourceAlreadyExistsException("Email already registered.");
        }

        if (userRepository.existsByMobile(request.mobile())) {
            log.error("======================Mobile number already registered======================");
            throw new ResourceAlreadyExistsException("Mobile number already registered.");
        }

        Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() ->
                        new ResourceNotFoundException("CUSTOMER role not found."));

        User user = new User();
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setMobile(request.mobile());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        if (user.getRoles() == null) {
            user.setRoles(new HashSet<>());
        }
        user.getRoles().add(customerRole);
        user.setEmailVerified(false);
        user.setMobileVerified(false);

        User savedUser = userRepository.save(user);
        log.error("======================User registered successfully======================");

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .mobile(savedUser.getMobile())
                .message("User registered successfully")
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        CustomUserDetails userDetails = new CustomUserDetails(user);

        String token = jwtService.generateToken(userDetails);

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .build();
    }
}