package com.localfix.service.service.impl;

import com.localfix.common.exception.ResourceAlreadyExistsException;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.service.dto.request.CreateServiceRequest;
import com.localfix.service.dto.request.UpdateServiceRequest;
import com.localfix.service.dto.response.ServiceResponse;
import com.localfix.service.repository.ServiceRepository;
import com.localfix.service.service.ServiceManagementService;
import com.localfix.servicecategory.dto.response.PageResponse;
import com.localfix.servicecategory.entity.ServiceCategory;
import com.localfix.servicecategory.repository.ServiceCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceManagementServiceImpl
        implements ServiceManagementService {

    private final ServiceRepository serviceRepository;

    private final ServiceCategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public ServiceResponse create(CreateServiceRequest request) {

        ServiceCategory category = categoryRepository
                .findByIdAndActiveTrue(request.categoryId()).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found or inactive."
                        ));

        if(serviceRepository.existsByCategoryIdAndNameIgnoreCase(request.categoryId(),request.name()))
        {
            throw new ResourceAlreadyExistsException(
                    "Service already exists in this category.");
        }

        com.localfix.service.entity.Service service= com.localfix.service.entity.Service.builder()
                .name(request.name())
                .basePrice(request.basePrice())
                .estimatedDuration(request.estimatedDuration())
                .description(request.description())
                .category(category).build();

        service.setActive(true);

        com.localfix.service.entity.Service savedService = serviceRepository.save(service);

        return ServiceResponse.builder()
                .id(savedService.getId())
                .categoryId(category.getId())
                .categoryName(category.getName())
                .name(savedService.getName())
                .description(savedService.getDescription())
                .estimatedDuration(savedService.getEstimatedDuration())
                .basePrice(savedService.getBasePrice())
                .active(savedService.getActive())
                .build();
    }

    @Override
    public ServiceResponse update(
            UUID id,
            UpdateServiceRequest request) {

        com.localfix.service.entity.Service service=serviceRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Service not found"));

        ServiceCategory category = categoryRepository
                .findByIdAndActiveTrue(request.categoryId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found or inactive."
                        ));

        boolean duplicate =
                serviceRepository
                        .existsByCategoryIdAndNameIgnoreCaseAndIdNot(
                                request.categoryId(),
                                request.name(),
                                id
                        );

        if (duplicate) {
            throw new ResourceAlreadyExistsException(
                    "Service already exists in this category."
            );
        }

        service.setCategory(category);
        service.setName(request.name());
        service.setDescription(request.description());
        service.setEstimatedDuration(
                request.estimatedDuration()
        );
        service.setBasePrice(
                request.basePrice()
        );

        if (request.active() != null) {
            service.setActive(request.active());
        }

        com.localfix.service.entity.Service updatedService =
                serviceRepository.save(service);

        return mapToResponse(updatedService);

    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse getById(UUID id) {

        com.localfix.service.entity.Service service = serviceRepository
                .findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Service not found or inactive."
                        ));

        return mapToResponse(service);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ServiceResponse> getAll(
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.ASC,
                        "name"
                )
        );

        Page<com.localfix.service.entity.Service> servicePage =
                serviceRepository.findAllByActiveTrue(
                        pageable
                );

        return mapToPageResponse(servicePage);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ServiceResponse> getByCategory(
                                        UUID categoryId,
                                        int page,
                                        int size
                                        ) {

        // Verify category exists and is active
        categoryRepository.findByIdAndActiveTrue(categoryId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Category not found or inactive."
                        ));

        Pageable pageable=PageRequest.of(page,size);

        return mapToPageResponse(serviceRepository
                .findByCategoryIdAndActiveTrue(categoryId,pageable));
    }

    @Override
    public void delete(UUID id) {

        com.localfix.service.entity.Service service=serviceRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Service not found"));

        if (!service.getActive()) {
            throw new IllegalStateException(
                    "Service is already inactive."
            );
        }

        service.setActive(false);

        serviceRepository.save(service);

    }

    @Override
    public void reActive(UUID id) {

        com.localfix.service.entity.Service service=serviceRepository.findById(id).orElseThrow(()->
                new ResourceNotFoundException("Service not found"));

        if (service.getActive()) {
            throw new IllegalStateException(
                    "Service is already active."
            );
        }

        service.setActive(true);

        serviceRepository.save(service);

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ServiceResponse> search(
            String keyword,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.ASC,
                        "name"
                )
        );

        Page<com.localfix.service.entity.Service> servicePage =
                serviceRepository
                        .findByNameContainingIgnoreCaseAndActiveTrue(keyword,pageable);

        return mapToPageResponse(servicePage);
    }

    private PageResponse<ServiceResponse> mapToPageResponse(
            Page<com.localfix.service.entity.Service> servicePage) {

        return PageResponse.<ServiceResponse>builder()
                .content(
                        servicePage.getContent()
                                .stream()
                                .map(this::mapToResponse)
                                .toList()
                )
                .page(servicePage.getNumber())
                .size(servicePage.getSize())
                .totalElements(servicePage.getTotalElements())
                .totalPages(servicePage.getTotalPages())
                .first(servicePage.isFirst())
                .last(servicePage.isLast())
                .build();
    }

    private ServiceResponse mapToResponse(com.localfix.service.entity.Service service) {

        return ServiceResponse.builder()
                .id(service.getId())
                .categoryId(service.getCategory().getId())
                .categoryName(service.getCategory().getName())
                .name(service.getName())
                .description(service.getDescription())
                .estimatedDuration(service.getEstimatedDuration())
                .basePrice(service.getBasePrice())
                .active(service.getActive())
                .build();
    }
}