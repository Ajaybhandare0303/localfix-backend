package com.localfix.provider.service.impl;

import com.localfix.common.enums.RoleType;
import com.localfix.common.exception.ResourceAlreadyExistsException;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.provider.dto.request.CreateProviderRequest;
import com.localfix.provider.dto.request.ProviderStatusRequest;
import com.localfix.provider.dto.request.UpdateProviderRequest;
import com.localfix.provider.dto.response.ProviderListResponse;
import com.localfix.provider.dto.response.ProviderResponse;
import com.localfix.provider.entity.Provider;
import com.localfix.provider.repository.ProviderRepository;
import com.localfix.provider.service.ProviderService;
import com.localfix.provider.specification.ProviderSpecification;
import com.localfix.servicecategory.dto.response.PageResponse;
import com.localfix.user.entity.User;
import com.localfix.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl
        implements ProviderService {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public ProviderResponse create(
            CreateProviderRequest request) {

        // 1. Get authenticated user's email
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        // 2. Find user
        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found."
                        ));

        // 3. Check whether user already has provider profile
        if (providerRepository.existsByUserId(user.getId())) {

            throw new ResourceAlreadyExistsException(
                    "Provider profile already exists."
            );
        }

        // 4. Check business name
        if (providerRepository
                .existsByBusinessNameIgnoreCase(
                        request.businessName())) {

            throw new ResourceAlreadyExistsException(
                    "Business name already exists."
            );
        }

        // 5. Create provider
        Provider provider = Provider.builder()
                .user(user)
                .businessName(request.businessName())
                .description(request.description())
                .address(request.address())
                .city(request.city())
                .state(request.state())
                .pincode(request.pincode())
                .experience(request.experience())
                .active(true)
                .verified(false)
                .build();

        // 6. Save
        Provider savedProvider =
                providerRepository.save(provider);

        // 7. Response
        return mapToResponse(savedProvider);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProviderListResponse> getAllProviders(
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.ASC,
                        "businessName"
                )
        );

        Page<Provider> providerPage =
                providerRepository
                        .findAllByActiveTrueAndVerifiedTrue(
                                pageable
                        );

        return mapToPageResponse(providerPage);
    }


    @Override
    @Transactional(readOnly = true)
    public ProviderResponse getProviderById(
            UUID providerId) {

        Provider provider =
                providerRepository
                        .findByIdAndActiveTrueAndVerifiedTrue(
                                providerId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Provider not found or not verified"
                                ));

        return mapToResponse(provider);
    }


    @Override
    @Transactional(readOnly = true)
    public ProviderResponse getMyProfile() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found."
                        ));

        Provider provider =
                providerRepository
                        .findByUserId(user.getId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Provider profile not found."
                                ));

        return mapToResponse(provider);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProviderListResponse> searchProviders(
            String keyword,
            String city,
            String state,
            UUID serviceId,
            UUID categoryId,
            int page,
            int size) {

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.ASC,
                                "businessName"
                        )
                );

        Specification<Provider> specification =
                ProviderSpecification.search(
                        keyword,
                        city,
                        state,
                        serviceId,
                        categoryId
                );

        Page<Provider> providerPage =
                providerRepository.findAll(
                        specification,
                        pageable
                );

        return mapToPageResponse(providerPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProviderListResponse>
    getProvidersByService(
            UUID serviceId,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.ASC,
                        "businessName"
                )
        );

        Page<Provider> providerPage =
                providerRepository
                        .findVerifiedProvidersByService(
                                serviceId,
                                pageable
                        );

        return mapToPageResponse(providerPage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProviderListResponse>
    getProvidersByCategory(
            UUID categoryId,
            int page,
            int size) {

        org.springframework.data.domain.Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.ASC,
                        "businessName"
                )
        );

        Page<Provider> providerPage =
                providerRepository
                        .findVerifiedProvidersByCategory(
                                categoryId,
                                pageable
                        );

        return mapToPageResponse(providerPage);
    }



    @Override
    @Transactional
    public ProviderResponse updateMyProfile(
            UpdateProviderRequest request) {

        Provider provider =
                getAuthenticatedProvider();

        provider.setBusinessName(
                request.businessName()
        );

        provider.setDescription(
                request.description()
        );

        provider.setAddress(
                request.address()
        );

        provider.setCity(
                request.city()
        );

        provider.setState(
                request.state()
        );

        provider.setPincode(
                request.pincode()
        );

        provider.setExperience(
                request.experience()
        );

        return mapToResponse(
                providerRepository.save(provider)
        );
    }


    @Override
    @Transactional
    public ProviderResponse updateMyStatus(
            ProviderStatusRequest request) {

        Provider provider =
                getAuthenticatedProvider();

        provider.setActive(
                request.active()
        );

        return mapToResponse(
                providerRepository.save(provider)
        );
    }


    private Provider getAuthenticatedProvider() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found."
                        ));

        return providerRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Provider profile not found."
                        ));
    }


    private ProviderResponse mapToResponse(
            Provider provider) {

        return ProviderResponse.builder()
                .id(provider.getId())
                .userId(provider.getUser().getId())
                .businessName(
                        provider.getBusinessName()
                )
                .description(
                        provider.getDescription()
                )
                .address(
                        provider.getAddress()
                )
                .city(provider.getCity())
                .state(provider.getState())
                .pincode(provider.getPincode())
                .experience(
                        provider.getExperience()
                )
                .active(provider.getActive())
                .verified(provider.getVerified())
                .build();
    }

    private PageResponse<ProviderListResponse>
    mapToPageResponse(
            Page<Provider> providerPage) {

        return PageResponse.<ProviderListResponse>builder()
                .content(
                        providerPage
                                .getContent()
                                .stream()
                                .map(this::mapToListResponse)
                                .toList()
                )
                .page(providerPage.getNumber())
                .size(providerPage.getSize())
                .totalElements(
                        providerPage.getTotalElements()
                )
                .totalPages(
                        providerPage.getTotalPages()
                )
                .first(
                        providerPage.isFirst()
                )
                .last(
                        providerPage.isLast()
                )
                .build();
    }

    private ProviderListResponse mapToListResponse(
            Provider provider) {

        return ProviderListResponse.builder()
                .id(provider.getId())
                .businessName(
                        provider.getBusinessName()
                )
                .description(
                        provider.getDescription()
                )
                .city(provider.getCity())
                .state(provider.getState())
                .experience(
                        provider.getExperience()
                )
                .verified(provider.getVerified())
                .build();
    }

}