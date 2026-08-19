package com.localfix.servicecategory.service.impl;

import com.localfix.common.exception.ResourceAlreadyExistsException;
import com.localfix.common.exception.ResourceNotFoundException;
import com.localfix.servicecategory.dto.response.CategoryResponse;
import com.localfix.servicecategory.dto.request.CreateCategoryRequest;
import com.localfix.servicecategory.dto.request.UpdateCategoryRequest;
import com.localfix.servicecategory.entity.ServiceCategory;
import com.localfix.servicecategory.repository.ServiceCategoryRepository;
import com.localfix.servicecategory.service.ServiceCategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ServiceCategoryServiceImpl implements ServiceCategoryService {

    private final ServiceCategoryRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {

        if(repository.existsByNameIgnoreCase(request.name()))
        {
            throw new ResourceAlreadyExistsException(
                    "Category already exists.");
        }

        ServiceCategory serviceCategory =ServiceCategory.builder()
                .name(request.name())
                .description(request.description())
                .icon(request.icon()).build();

        ServiceCategory savedCategory=repository.save(serviceCategory);

        return CategoryResponse.builder()
                .id(savedCategory.getId())
                .name(savedCategory.getName())
                .description(savedCategory.getDescription())
                .icon(savedCategory.getIcon())
                .active(savedCategory.getActive())
                .build();

    }

    @Override
    @Transactional
    public CategoryResponse update(UUID id, UpdateCategoryRequest request) {

        ServiceCategory category = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        // Check duplicate name
        repository.findByNameIgnoreCase(request.name())
                .ifPresent(existingCategory -> {

                    if (!existingCategory.getId().equals(id)) {
                        throw new ResourceAlreadyExistsException(
                                "Category name already exists.");
                    }
                });

        category.setName(request.name());
        category.setDescription(request.description());
        category.setIcon(request.icon());

        if (request.active() != null) {
            category.setActive(request.active());
        }

        ServiceCategory updatedCategory = repository.save(category);

        return CategoryResponse.builder()
                .id(updatedCategory.getId())
                .name(updatedCategory.getName())
                .icon(updatedCategory.getIcon())
                .description(updatedCategory.getDescription())
                .active(updatedCategory.getActive()).build();
    }

    @Override
    @Transactional
    public CategoryResponse getById(UUID id) {

        ServiceCategory serviceCategory = repository.findByIdAndActiveTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        return CategoryResponse.builder()
                .id(serviceCategory.getId())
                .name(serviceCategory.getName())
                .description(serviceCategory.getDescription())
                .icon(serviceCategory.getIcon())
                .active(serviceCategory.getActive())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {

        return repository.findAllByActiveTrue()
                .stream()
                .map(category -> {
                    return CategoryResponse.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .description(category.getDescription())
                            .icon(category.getIcon())
                            .active(category.getActive())
                            .build();
                }).toList();
    }

    @Override
    @Transactional
    public void delete(UUID id) {

        ServiceCategory category = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found."));

        if (!Boolean.TRUE.equals(category.getActive())) {
            throw new ResourceAlreadyExistsException(
                    "Category is already inactive.");
        }

        category.setActive(false);

        repository.save(category);
    }
}
