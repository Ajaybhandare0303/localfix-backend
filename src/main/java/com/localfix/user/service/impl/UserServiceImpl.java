package com.localfix.user.service.impl;

import com.localfix.common.config.SecurityUtils;
import com.localfix.common.exception.InvalidPasswordException;
import com.localfix.common.exception.ResourceAlreadyExistsException;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.user.dto.request.ChangePasswordRequest;
import com.localfix.user.dto.request.UpdateProfileRequest;
import com.localfix.user.dto.response.UserProfileResponse;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;
import com.localfix.user.role.entity.Role;
import com.localfix.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public UserProfileResponse getCurrentUser() {

        String email= SecurityUtils.getCurrentUsername();

        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Set<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        return UserProfileResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .accountStatus(user.getAccountStatus())
                .emailVerified(user.getEmailVerified())
                .mobileVerified(user.getMobileVerified())
                .roles(roles)
                .build();

    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {

        // Get logged-in user's email from JWT
        String email = SecurityUtils.getCurrentUsername();

        // Fetch user with roles
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Check if another user already has this mobile number
        if (userRepository.existsByMobileAndEmailNot(request.mobile(), email)) {
            throw new ResourceAlreadyExistsException("Mobile number already exists.");
        }

        // Update allowed fields
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setMobile(request.mobile());

        // Save updated user
        User updatedUser = userRepository.save(user);

        // Convert roles
        Set<String> roles = updatedUser.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        // Return DTO
        return UserProfileResponse.builder()
                .id(updatedUser.getId())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .email(updatedUser.getEmail())
                .mobile(updatedUser.getMobile())
                .accountStatus(updatedUser.getAccountStatus())
                .emailVerified(updatedUser.getEmailVerified())
                .mobileVerified(updatedUser.getMobileVerified())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        // Get logged-in user's email
        String email = SecurityUtils.getCurrentUsername();

        // Fetch user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect.");
        }

        // Verify new password and confirm password
        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new InvalidPasswordException("New password and confirm password do not match.");
        }

        // Prevent using the same password
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new InvalidPasswordException("New password must be different from the current password.");
        }

        // Encode and save
        user.setPassword(passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);
    }


}