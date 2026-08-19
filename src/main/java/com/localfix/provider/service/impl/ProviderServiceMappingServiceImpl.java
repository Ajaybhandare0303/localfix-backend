package com.localfix.provider.service.impl;

import com.localfix.common.exception.ResourceAlreadyExistsException;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.provider.entity.Provider;
import com.localfix.provider.repository.ProviderRepository;
import com.localfix.provider.service.ProviderServiceMappingService;
import com.localfix.provider.services.dto.request.AddProviderServiceRequest;
import com.localfix.provider.services.dto.response.ProviderServiceResponse;
import com.localfix.provider.services.entity.ProviderServiceMapping;
import com.localfix.provider.services.repository.ProviderServiceRepository;
import com.localfix.service.entity.Service;
import com.localfix.service.repository.ServiceRepository;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ProviderServiceMappingServiceImpl
        implements ProviderServiceMappingService {

    private final ProviderServiceRepository providerServiceRepository;

    private final ProviderRepository providerRepository;

    private final ServiceRepository serviceRepository;

    private final UserRepository userRepository;


    @Override
    @Transactional
    public ProviderServiceResponse addService(
            AddProviderServiceRequest request) {

        Provider provider =
                getAuthenticatedProvider();

        Service service =
                serviceRepository
                        .findByIdAndActiveTrue(
                                request.serviceId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service not found or inactive."
                                ));

        if (providerServiceRepository
                .existsByProviderIdAndServiceId(
                        provider.getId(),
                        service.getId())) {

            throw new ResourceAlreadyExistsException(
                    "Service is already added to this provider."
            );
        }

        ProviderServiceMapping mapping =
                ProviderServiceMapping.builder()
                        .provider(provider)
                        .service(service)
                        .active(true)
                        .build();

        ProviderServiceMapping saved =
                providerServiceRepository.save(mapping);

        return mapToResponse(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<ProviderServiceResponse> getMyServices() {

        Provider provider =
                getAuthenticatedProvider();

        return providerServiceRepository
                .findAllByProviderIdAndActiveTrue(
                        provider.getId()
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional
    public void removeService(UUID serviceId) {

        Provider provider =
                getAuthenticatedProvider();

        ProviderServiceMapping mapping =
                providerServiceRepository
                        .findByProviderIdAndServiceId(
                                provider.getId(),
                                serviceId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Service is not assigned to this provider."
                                ));

        mapping.setActive(false);

        providerServiceRepository.save(mapping);
    }


    private Provider getAuthenticatedProvider() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Authenticated user not found."
                                ));

        return providerRepository
                .findByUserIdAndActiveTrue(
                        user.getId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active provider profile not found."
                        ));
    }


    private ProviderServiceResponse mapToResponse(
            ProviderServiceMapping mapping) {

        Service service = mapping.getService();

        return ProviderServiceResponse.builder()
                .id(mapping.getId())
                .providerId(
                        mapping.getProvider().getId()
                )
                .serviceId(service.getId())
                .serviceName(service.getName())
                .categoryId(
                        service.getCategory().getId()
                )
                .categoryName(
                        service.getCategory().getName()
                )
                .basePrice(
                        service.getBasePrice()
                )
                .estimatedDuration(
                        service.getEstimatedDuration()
                )
                .active(mapping.getActive())
                .build();
    }
}