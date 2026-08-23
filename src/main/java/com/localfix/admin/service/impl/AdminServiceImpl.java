package com.localfix.admin.service.impl;

import com.localfix.admin.service.AdminService;
import com.localfix.common.enums.RoleType;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.common.exception.VerifyProvider;
import com.localfix.provider.entity.Provider;
import com.localfix.provider.repository.ProviderRepository;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService
{

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Provider verifyProvider(UUID providerId) {

        Provider provider = providerRepository.findById(providerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Provider not found with id: " + providerId));

        User authenticatedUser = getAuthenticatedUser();

        boolean isAdmin = authenticatedUser.getRoles()
                .stream()
                .anyMatch(role -> role.getName() == RoleType.ADMIN);

        if (!isAdmin) {
            throw new VerifyProvider(
                    "You are not authorized to verify a provider");
        }

        if (provider.getVerified()==true) {
            throw new VerifyProvider("Provider is already verified");
        }

        provider.setVerified(true);
        return provider;
    }

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found."
                        ));
    }

}
